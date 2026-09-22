package com.smartwms.warehouse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_warehouse")
public class Warehouse {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String warehouseCode;

    private String warehouseName;

    private String address;

    private String contactPerson;

    private String phone;

    private Integer totalCapacity;

    private Integer usedCapacity;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
