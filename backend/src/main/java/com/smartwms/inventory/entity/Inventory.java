package com.smartwms.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_inventory")
public class Inventory {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long warehouseId;

    private Long locationId;

    private Long skuId;

    private Long batchId;

    private Integer totalQty;

    private Integer availableQty;

    private Integer lockedQty;

    private Integer damagedQty;

    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String warehouseName;

    @TableField(exist = false)
    private String locationCode;

    @TableField(exist = false)
    private String skuCode;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String batchNo;

    @TableField(exist = false)
    private String status;
}
