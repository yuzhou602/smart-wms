package com.smartwms.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_task")
public class Task {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String taskNo;

    private String taskType;

    private String sourceOrderNo;

    private Long warehouseId;

    private String status;

    private Integer priority;

    private Long assigneeId;

    private String assigneeName;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
