package com.smartwms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    CREATED("CREATED", "已创建"),
    RECEIVING("RECEIVING", "收货中"),
    RECEIVED("RECEIVED", "已收货"),
    QUALITY_CHECK("QUALITY_CHECK", "质检中"),
    PUTAWAY("PUTAWAY", "上架中"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消"),
    ON_HOLD("ON_HOLD", "暂停中");

    private final String code;
    private final String description;
}
