package com.smartwms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertLevel {

    LOW("LOW", "低风险"),
    MEDIUM("MEDIUM", "中风险"),
    HIGH("HIGH", "高风险"),
    CRITICAL("CRITICAL", "严重");

    private final String code;
    private final String description;
}
