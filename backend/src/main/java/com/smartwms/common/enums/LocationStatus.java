package com.smartwms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LocationStatus {

    EMPTY("EMPTY", "空闲"),
    AVAILABLE("AVAILABLE", "可用"),
    PARTIAL("PARTIAL", "部分占用"),
    FULL("FULL", "已满"),
    LOCKED("LOCKED", "锁定"),
    DISABLED("DISABLED", "禁用");

    private final String code;
    private final String description;
}
