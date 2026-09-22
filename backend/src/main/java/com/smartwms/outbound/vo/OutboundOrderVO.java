package com.smartwms.outbound.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OutboundOrderVO {

    private Long id;
    private String orderNo;
    private String orderType;
    private Long warehouseId;
    private String warehouseName;
    private String customerName;
    private String status;
    private Integer totalQty;
    private Integer pickedQty;
    private Integer shippedQty;
    private LocalDate expectedDate;
    private LocalDateTime createdAt;
}
