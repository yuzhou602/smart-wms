package com.smartwms.inbound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("wms_inbound_order")
public class InboundOrder {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String orderNo;

    private String orderType;

    private Long warehouseId;

    private Long supplierId;

    private String status;

    private Integer totalQty;

    private Integer receivedQty;

    private Integer putawayQty;

    private LocalDate expectedDate;

    private LocalDate actualDate;

    private String remark;

    private Long createdBy;

    private Long approvedBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String warehouseName;

    @TableField(exist = false)
    private String supplierName;
}
