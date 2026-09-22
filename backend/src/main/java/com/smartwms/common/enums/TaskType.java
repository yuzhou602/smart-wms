package com.smartwms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskType {

    PUTAWAY("PUTAWAY", "上架任务"),
    PICKING("PICKING", "拣货任务"),
    STOCKTAKE("STOCKTAKE", "盘点任务"),
    REPLENISHMENT("REPLENISHMENT", "补货任务"),
    TRANSFER("TRANSFER", "调拨任务");

    private final String code;
    private final String description;
}
