package com.smartwms.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_supplier")
public class Supplier {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String supplierCode;

    private String supplierName;

    private String contactPerson;

    private String phone;

    private String email;

    private String address;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
