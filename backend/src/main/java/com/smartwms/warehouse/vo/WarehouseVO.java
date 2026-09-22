package com.smartwms.warehouse.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WarehouseVO {

    private Long id;
    private String warehouseCode;
    private String warehouseName;
    private String address;
    private String contactPerson;
    private String phone;
    private Integer totalCapacity;
    private Integer usedCapacity;
    private Integer status;
    private LocalDateTime createdAt;
}
