package com.smartwms.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wms_sku")
public class SKU {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String skuCode;

    private Long productId;

    private String specification;

    private String unit;

    private String barcode;

    private BigDecimal weight;

    private BigDecimal volume;

    private Integer safetyStock;

    private Integer maxStock;

    private String outboundStrategy;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String productName;
}
