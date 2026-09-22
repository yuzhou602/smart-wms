package com.smartwms.stocktake.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_stocktake")
public class Stocktake {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String stocktakeNo;

    private String stocktakeType;

    private Long warehouseId;

    private String status;

    private Integer totalItems;

    @TableField("completed_items")
    private Integer countedItems;

    @TableField("difference_items")
    private Integer diffItems;

    private String remark;

    private Long createdBy;

    private Long assignedTo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
