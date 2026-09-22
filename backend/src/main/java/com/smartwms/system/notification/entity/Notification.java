package com.smartwms.system.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_notification")
public class Notification {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private String type;

    private Integer isRead;

    @TableField(exist = false)
    private String relatedType;

    @TableField(exist = false)
    private Long relatedId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
