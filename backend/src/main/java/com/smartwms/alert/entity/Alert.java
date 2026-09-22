package com.smartwms.alert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_inventory_alert")
public class Alert {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String alertType;

    private String alertLevel;

    private Long skuId;

    private Long warehouseId;

    private Long locationId;

    private Long batchId;

    private String title;

    private String content;

    private String currentValue;

    private String thresholdValue;

    private String suggestion;

    private Integer isHandled;

    private Long handledBy;

    private LocalDateTime handledAt;

    private String handleResult;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
