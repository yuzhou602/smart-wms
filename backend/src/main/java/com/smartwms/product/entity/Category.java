package com.smartwms.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_category")
public class Category {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long parentId;

    private String categoryName;

    private Integer level;

    private Integer sortOrder;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
