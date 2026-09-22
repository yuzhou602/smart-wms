package com.smartwms.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.ResultCode;
import com.smartwms.system.role.entity.Role;
import com.smartwms.system.role.entity.RolePermission;
import com.smartwms.system.role.mapper.RoleMapper;
import com.smartwms.system.role.mapper.RolePermissionMapper;
import com.smartwms.system.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public List<Role> listRoles(String keyword) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Role::getRoleName, keyword)
                    .or().like(Role::getRoleCode, keyword);
        }
        wrapper.orderByAsc(Role::getSortOrder);
        return roleMapper.selectList(wrapper);
    }

    @Override
    public Role getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return role;
    }

    @Override
    @Transactional
    public Role createRole(Role role) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, role.getRoleCode());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }
        roleMapper.insert(role);
        return role;
    }

    @Override
    @Transactional
    public Role updateRole(Long id, Role role) {
        Role existing = roleMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        role.setId(id);
        roleMapper.updateById(role);
        return role;
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        roleMapper.deleteById(id);
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, id);
        rolePermissionMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        getRoleById(roleId);
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);

        for (Long permissionId : permissionIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        }
    }

    @Override
    public List<Long> getPermissionIds(Long roleId) {
        getRoleById(roleId);
        return rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId))
                .stream()
                .map(RolePermission::getPermissionId)
                .toList();
    }
}
