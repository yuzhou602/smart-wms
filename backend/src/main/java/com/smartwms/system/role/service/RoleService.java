package com.smartwms.system.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.system.role.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {

    List<Role> listRoles(String keyword);

    Role getRoleById(Long id);

    Role createRole(Role role);

    Role updateRole(Long id, Role role);

    void deleteRole(Long id);

    void assignPermissions(Long roleId, List<Long> permissionIds);

    List<Long> getPermissionIds(Long roleId);
}
