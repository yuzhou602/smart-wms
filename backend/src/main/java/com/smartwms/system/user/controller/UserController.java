package com.smartwms.system.user.controller;

import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.security.UserPrincipal;
import com.smartwms.system.user.dto.PasswordUpdateRequest;
import com.smartwms.system.user.dto.UserCreateRequest;
import com.smartwms.system.user.dto.UserUpdateRequest;
import com.smartwms.system.user.service.UserService;
import com.smartwms.system.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理", description = "用户CRUD操作")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping
    @PreAuthorize("hasAuthority('user:management')")
    public R<PageResult<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        return R.ok(userService.listUsers(page, pageSize, keyword));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:management')")
    public R<UserVO> getUserById(@PathVariable Long id) {
        return R.ok(userService.getUserById(id));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public R<UserVO> createUser(@Valid @RequestBody UserCreateRequest request) {
        return R.ok(userService.createUser(request));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public R<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return R.ok(userService.updateUser(id, request));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public R<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.ok();
    }

    @Operation(summary = "分配角色")
    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('user:assign')")
    public R<Void> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return R.ok();
    }

    @Operation(summary = "获取用户已分配角色")
    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('user:management')")
    public R<List<Long>> getRoleIds(@PathVariable Long userId) {
        return R.ok(userService.getRoleIds(userId));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/{userId}/password")
    public R<Void> updatePassword(@PathVariable Long userId,
                                  @Valid @RequestBody PasswordUpdateRequest request,
                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        if (!currentUser.getUserId().equals(userId)) {
            throw new com.smartwms.common.exception.BusinessException("只能修改自己的密码");
        }
        userService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());
        return R.ok();
    }
}
