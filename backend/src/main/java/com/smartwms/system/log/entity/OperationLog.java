package com.smartwms.system.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class OperationLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String username;

    private String module;

    private String operation;

    @TableField("target")
    private String description;

    private String method;

    private String url;

    private String params;

    private String ip;

    @TableField(exist = false)
    private String userAgent;

    @TableField("result")
    private Integer status;

    private String errorMsg;

    private Long duration;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
