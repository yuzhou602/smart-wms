package com.smartwms.outbound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("wms_outbound_order")
public class OutboundOrder {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String orderNo;

    private String orderType;

    private Long warehouseId;

    private String customerName;

    private String status;

    private Integer totalQty;

    private Integer pickedQty;

    private Integer shippedQty;

    private LocalDate expectedDate;

    private LocalDate actualDate;

    private String remark;

    private Long createdBy;

    private Long approvedBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
