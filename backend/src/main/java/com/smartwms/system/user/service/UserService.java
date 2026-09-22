package com.smartwms.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.common.response.PageResult;
import com.smartwms.system.user.dto.LoginRequest;
import com.smartwms.system.user.dto.UserCreateRequest;
import com.smartwms.system.user.dto.UserUpdateRequest;
import com.smartwms.system.user.entity.User;
import com.smartwms.system.user.vo.LoginResponse;
import com.smartwms.system.user.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {

    LoginResponse login(LoginRequest request);

    UserVO getCurrentUser();

    PageResult<UserVO> listUsers(int page, int pageSize, String keyword);

    UserVO getUserById(Long id);

    UserVO createUser(UserCreateRequest request);

    UserVO updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    void assignRoles(Long userId, List<Long> roleIds);

    List<Long> getRoleIds(Long userId);

    void updatePassword(Long userId, String oldPassword, String newPassword);
}
