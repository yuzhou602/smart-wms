package com.smartwms.product.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductVO {

    private Long id;
    private String productCode;
    private String productName;
    private Long categoryId;
    private String categoryName;
    private String brand;
    private String description;
    private String image;
    private Integer status;
    private Integer skuCount;
    private LocalDateTime createdAt;
}
