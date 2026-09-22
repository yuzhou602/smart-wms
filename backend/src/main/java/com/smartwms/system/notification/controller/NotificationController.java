package com.smartwms.system.notification.controller;

import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.security.SecurityUtils;
import com.smartwms.system.notification.entity.Notification;
import com.smartwms.system.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "通知管理", description = "用户通知管理")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "获取通知列表")
    @GetMapping
    public R<PageResult<Notification>> listNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Boolean isRead) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(notificationService.listNotifications(userId, page, pageSize, isRead));
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public R<Long> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(notificationService.countUnread(userId));
    }

    @Operation(summary = "标记通知为已读")
    @PostMapping("/{id}/read")
    public R<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id, SecurityUtils.getCurrentUserId());
        return R.ok();
    }

    @Operation(summary = "标记所有通知为已读")
    @PostMapping("/read-all")
    public R<Void> markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return R.ok();
    }
}
