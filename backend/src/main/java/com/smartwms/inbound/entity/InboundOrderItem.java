package com.smartwms.inbound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("wms_inbound_order_item")
public class InboundOrderItem {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long orderId;

    private Long skuId;

    private Integer expectedQty;

    private Integer receivedQty;

    private Integer putawayQty;

    private String batchNo;

    private LocalDate productionDate;

    private LocalDate expiryDate;

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
