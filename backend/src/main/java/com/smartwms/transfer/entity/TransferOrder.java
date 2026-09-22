package com.smartwms.transfer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_transfer_order")
public class TransferOrder {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String orderNo;

    private Long sourceWarehouseId;

    private Long targetWarehouseId;

    private String status;

    private Integer totalQty;

    private Integer shippedQty;

    private Integer receivedQty;

    private String remark;

    private Long createdBy;

    private Long approvedBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
