package com.smartwms.warehouse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_rack")
public class Rack {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long zoneId;

    private String rackCode;

    private String rackName;

    private Integer floorCount;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
