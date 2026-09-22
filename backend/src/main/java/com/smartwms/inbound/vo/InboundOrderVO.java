package com.smartwms.inbound.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InboundOrderVO {

    private Long id;
    private String orderNo;
    private String orderType;
    private Long warehouseId;
    private String warehouseName;
    private Long supplierId;
    private String supplierName;
    private String status;
    private Integer totalQty;
    private Integer receivedQty;
    private Integer putawayQty;
    private LocalDate expectedDate;
    private LocalDateTime createdAt;
}
