package com.smartwms.system.notification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.common.response.PageResult;
import com.smartwms.system.notification.entity.Notification;

public interface NotificationService extends IService<Notification> {

    PageResult<Notification> listNotifications(Long userId, int page, int pageSize, Boolean isRead);

    Long countUnread(Long userId);

    void markAsRead(Long id, Long userId);

    void markAllAsRead(Long userId);

    void createNotification(Long userId, String title, String content, String type);
}
