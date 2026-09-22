package com.smartwms.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.ResultCode;
import com.smartwms.security.JwtTokenProvider;
import com.smartwms.security.SecurityUtils;
import com.smartwms.system.user.dto.LoginRequest;
import com.smartwms.system.user.dto.UserCreateRequest;
import com.smartwms.system.user.dto.UserUpdateRequest;
import com.smartwms.system.user.entity.User;
import com.smartwms.system.user.entity.UserRole;
import com.smartwms.system.user.mapper.UserMapper;
import com.smartwms.system.user.mapper.UserRoleMapper;
import com.smartwms.system.user.service.UserService;
import com.smartwms.system.user.vo.LoginResponse;
import com.smartwms.system.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        List<String> permissions = userMapper.findPermissionsByUserId(user.getId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPermissions(permissions);
        return response;
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        UserVO vo = convertToVO(user);
        vo.setPermissions(userMapper.findPermissionsByUserId(userId));
        return vo;
    }

    @Override
    public PageResult<UserVO> listUsers(int page, int pageSize, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword);
        }
        wrapper.orderByDesc(User::getCreatedAt);

        Page<User> pageResult = userMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<UserVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO createUser(UserCreateRequest request) {
        User existing = userMapper.findByUsername(request.getUsername());
        if (existing != null) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        userMapper.insert(user);
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO updateUser(Long id, UserUpdateRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        userMapper.updateById(user);
        return convertToVO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        userRoleMapper.deleteByUserId(userId);

        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    public List<Long> getRoleIds(Long userId) {
        getUserById(userId);
        return userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId))
                .stream()
                .map(UserRole::getRoleId)
                .toList();
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        com.smartwms.common.util.PasswordValidator.ValidationResult result =
            com.smartwms.common.util.PasswordValidator.validate(newPassword);
        if (!result.isValid()) {
            throw new BusinessException(result.getMessage());
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }
}
