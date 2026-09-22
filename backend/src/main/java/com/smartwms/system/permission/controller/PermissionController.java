package com.smartwms.system.permission.controller;

import com.smartwms.common.response.R;
import com.smartwms.system.permission.entity.Permission;
import com.smartwms.system.permission.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "权限管理", description = "权限CRUD操作")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('role:permission')")
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "获取权限列表")
    @GetMapping
    public R<List<Permission>> listPermissions() {
        return R.ok(permissionService.listPermissions());
    }

    @Operation(summary = "获取权限树")
    @GetMapping("/tree")
    public R<List<Permission>> getPermissionTree() {
        return R.ok(permissionService.getPermissionTree());
    }

    @Operation(summary = "获取权限详情")
    @GetMapping("/{id}")
    public R<Permission> getPermissionById(@PathVariable Long id) {
        return R.ok(permissionService.getPermissionById(id));
    }

    @Operation(summary = "创建权限")
    @PostMapping
    public R<Permission> createPermission(@RequestBody Permission permission) {
        return R.ok(permissionService.createPermission(permission));
    }

    @Operation(summary = "更新权限")
    @PutMapping("/{id}")
    public R<Permission> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        return R.ok(permissionService.updatePermission(id, permission));
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public R<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return R.ok();
    }
}
