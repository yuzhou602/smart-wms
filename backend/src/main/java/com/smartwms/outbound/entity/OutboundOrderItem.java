package com.smartwms.outbound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_outbound_order_item")
public class OutboundOrderItem {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long orderId;

    private Long skuId;

    private Integer requiredQty;

    private Integer allocatedQty;

    private Integer pickedQty;

    private Integer lockedQty;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String skuCode;

    @TableField(exist = false)
    private String productName;
}
