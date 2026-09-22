package com.smartwms.transfer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_transfer_order_item")
public class TransferOrderItem {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long orderId;

    private Long skuId;

    private Long batchId;

    private Integer qty;

    private Integer shippedQty;

    private Integer receivedQty;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String skuCode;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String batchNo;
}
