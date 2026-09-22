package com.smartwms.warehouse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_location")
public class Location {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long rackId;

    private Long zoneId;

    private Long warehouseId;

    private String locationCode;

    private Integer floor;

    private Integer position;

    private String locationType;

    private java.math.BigDecimal maxWeight;

    private Integer maxCapacity;

    private Integer usedCapacity;

    private String status;

    private Integer isDisabled;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
