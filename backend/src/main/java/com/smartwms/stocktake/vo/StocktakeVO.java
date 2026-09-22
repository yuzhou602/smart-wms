package com.smartwms.stocktake.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StocktakeVO {

    private Long id;
    private String stocktakeNo;
    private String stocktakeType;
    private Long warehouseId;
    private String warehouseName;
    private String status;
    private Integer totalItems;
    private Integer countedItems;
    private Integer diffItems;
    private LocalDateTime createdAt;
}
