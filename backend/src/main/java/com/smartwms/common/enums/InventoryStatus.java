package com.smartwms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InventoryStatus {

    AVAILABLE("AVAILABLE", "可用"),
    LOCKED("LOCKED", "锁定"),
    IN_TRANSIT("IN_TRANSIT", "在途"),
    DAMAGED("DAMAGED", "损坏"),
    QUARANTINE("QUARANTINE", "隔离");

    private final String code;
    private final String description;
}
