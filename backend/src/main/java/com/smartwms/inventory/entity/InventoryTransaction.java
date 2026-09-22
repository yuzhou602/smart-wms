package com.smartwms.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_inventory_transaction")
public class InventoryTransaction {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String transactionNo;

    private Long skuId;

    private Long warehouseId;

    private Long locationId;

    private Long batchId;

    private String transactionType;

    private Integer beforeQty;

    private Integer changeQty;

    private Integer afterQty;

    private String sourceOrderNo;

    private Long userId;

    private String username;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
