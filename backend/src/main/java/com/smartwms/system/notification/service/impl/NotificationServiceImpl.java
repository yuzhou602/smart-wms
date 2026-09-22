package com.smartwms.system.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.response.PageResult;
import com.smartwms.system.notification.entity.Notification;
import com.smartwms.system.notification.mapper.NotificationMapper;
import com.smartwms.system.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public PageResult<Notification> listNotifications(Long userId, int page, int pageSize, Boolean isRead) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead ? 1 : 0);
        }
        wrapper.orderByDesc(Notification::getCreatedAt);

        Page<Notification> pageResult = notificationMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<Notification> records = pageResult.getRecords();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public Long countUnread(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.eq(Notification::getIsRead, 0);
        return notificationMapper.selectCount(wrapper);
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Long userId) {
        Notification notification = notificationMapper.selectOne(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getId, id)
                .eq(Notification::getUserId, userId));
        if (notification != null) {
            notification.setIsRead(1);
            notificationMapper.updateById(notification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.eq(Notification::getIsRead, 0);

        Notification notification = new Notification();
        notification.setIsRead(1);
        notificationMapper.update(notification, wrapper);
    }

    @Override
    @Transactional
    public void createNotification(Long userId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }
}
