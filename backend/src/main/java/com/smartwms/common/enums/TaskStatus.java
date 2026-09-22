package com.smartwms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus {

    PENDING("PENDING", "待处理"),
    ASSIGNED("ASSIGNED", "已指派"),
    PROCESSING("PROCESSING", "处理中"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String description;
}
