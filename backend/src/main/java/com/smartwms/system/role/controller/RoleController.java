package com.smartwms.system.role.controller;

import com.smartwms.common.response.R;
import com.smartwms.system.role.entity.Role;
import com.smartwms.system.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理", description = "角色CRUD和权限分配")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('role:permission')")
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "获取角色列表")
    @GetMapping
    public R<List<Role>> listRoles(@RequestParam(required = false) String keyword) {
        return R.ok(roleService.listRoles(keyword));
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public R<Role> getRoleById(@PathVariable Long id) {
        return R.ok(roleService.getRoleById(id));
    }

    @Operation(summary = "创建角色")
    @PostMapping
    public R<Role> createRole(@RequestBody Role role) {
        return R.ok(roleService.createRole(role));
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    public R<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        return R.ok(roleService.updateRole(id, role));
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public R<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.ok();
    }

    @Operation(summary = "分配权限")
    @PostMapping("/{id}/permissions")
    public R<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return R.ok();
    }

    @Operation(summary = "获取角色已分配权限")
    @GetMapping("/{id}/permissions")
    public R<List<Long>> getPermissionIds(@PathVariable Long id) {
        return R.ok(roleService.getPermissionIds(id));
    }
}
