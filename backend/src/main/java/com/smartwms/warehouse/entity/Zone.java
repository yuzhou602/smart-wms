package com.smartwms.warehouse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_zone")
public class Zone {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long warehouseId;

    private String zoneCode;

    private String zoneName;

    private String zoneType;

    private String temperature;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
