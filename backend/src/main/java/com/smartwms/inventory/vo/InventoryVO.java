package com.smartwms.inventory.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryVO {

    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long locationId;
    private String locationCode;
    private Long skuId;
    private String skuCode;
    private String productName;
    private Long batchId;
    private String batchNo;
    private Integer totalQty;
    private Integer availableQty;
    private Integer lockedQty;
    private Integer damagedQty;
    private String status;
    private LocalDateTime createdAt;
}
