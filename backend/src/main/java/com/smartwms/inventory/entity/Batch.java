package com.smartwms.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("wms_batch")
public class Batch {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String batchNo;

    private Long skuId;

    private Long supplierId;

    private Integer quantity;

    private LocalDate productionDate;

    private LocalDate expiryDate;

    private String qualityStatus;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String skuCode;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String supplierName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
