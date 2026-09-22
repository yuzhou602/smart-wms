package com.smartwms.system.user.controller;

import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.R;
import com.smartwms.security.service.LoginRateLimitService;
import com.smartwms.system.user.dto.LoginRequest;
import com.smartwms.system.user.service.UserService;
import com.smartwms.system.user.vo.LoginResponse;
import com.smartwms.system.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "用户管理", description = "用户登录、注册、管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Autowired(required = false)
    private LoginRateLimitService loginRateLimitService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        if (loginRateLimitService != null && loginRateLimitService.isLocked(request.getUsername())) {
            long remainingTime = loginRateLimitService.getRemainingLockTime(request.getUsername());
            throw new BusinessException("账户已锁定，请在" + (remainingTime / 60) + "分钟后重试");
        }

        try {
            R<LoginResponse> response = R.ok(userService.login(request));
            if (loginRateLimitService != null) {
                loginRateLimitService.resetAttempts(request.getUsername());
            }
            return response;
        } catch (Exception e) {
            if (loginRateLimitService != null) {
                loginRateLimitService.recordFailedAttempt(request.getUsername());
            }
            throw e;
        }
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public R<UserVO> getCurrentUser() {
        return R.ok(userService.getCurrentUser());
    }
}
