package com.smartwms.transfer.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferOrderVO {

    private Long id;
    private String orderNo;
    private Long sourceWarehouseId;
    private String sourceWarehouseName;
    private Long targetWarehouseId;
    private String targetWarehouseName;
    private String status;
    private Integer totalQty;
    private Integer shippedQty;
    private Integer receivedQty;
    private String remark;
    private LocalDateTime createdAt;
}
