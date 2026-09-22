package com.smartwms.system.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_permission")
public class Permission {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String permissionName;

    private String permissionCode;

    private Long parentId;

    private Integer type;

    private Integer sortOrder;

    private String path;

    private String icon;

    private Integer status;

    @TableField(exist = false)
    private List<Permission> children;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
