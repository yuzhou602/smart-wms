package com.smartwms.stocktake.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_stocktake_item")
public class StocktakeItem {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long stocktakeId;

    private Long skuId;

    private Long locationId;

    private Long batchId;

    private Integer systemQty;

    private Integer actualQty;

    @TableField("difference_qty")
    private Integer diffQty;

    private String status;

    private String remark;

    @TableField(exist = false)
    private String skuCode;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String locationCode;

    @TableField(exist = false)
    private String batchNo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
