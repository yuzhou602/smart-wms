package com.smartwms.system.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.system.permission.entity.Permission;

import java.util.List;

public interface PermissionService extends IService<Permission> {

    List<Permission> listPermissions();

    List<Permission> getPermissionTree();

    Permission getPermissionById(Long id);

    Permission createPermission(Permission permission);

    Permission updatePermission(Long id, Permission permission);

    void deletePermission(Long id);
}
