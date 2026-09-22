package com.smartwms.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_product")
public class Product {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String productCode;

    private String productName;

    private Long categoryId;

    private String brand;

    private String description;

    private String image;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private Integer skuCount;
}
