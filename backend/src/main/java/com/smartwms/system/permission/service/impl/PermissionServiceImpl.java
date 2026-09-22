package com.smartwms.system.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.ResultCode;
import com.smartwms.system.permission.entity.Permission;
import com.smartwms.system.permission.mapper.PermissionMapper;
import com.smartwms.system.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public List<Permission> listPermissions() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Permission::getSortOrder);
        return permissionMapper.selectList(wrapper);
    }

    @Override
    public List<Permission> getPermissionTree() {
        List<Permission> allPermissions = listPermissions();
        return buildTree(allPermissions, 0L);
    }

    private List<Permission> buildTree(List<Permission> allPermissions, Long parentId) {
        return allPermissions.stream()
                .filter(p -> parentId.equals(p.getParentId()))
                .peek(p -> p.setChildren(buildTree(allPermissions, p.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Permission getPermissionById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return permission;
    }

    @Override
    @Transactional
    public Permission createPermission(Permission permission) {
        permissionMapper.insert(permission);
        return permission;
    }

    @Override
    @Transactional
    public Permission updatePermission(Long id, Permission permission) {
        Permission existing = permissionMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        permission.setId(id);
        permissionMapper.updateById(permission);
        return permission;
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        permissionMapper.deleteById(id);
    }
}
