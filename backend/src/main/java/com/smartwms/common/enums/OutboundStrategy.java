package com.smartwms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OutboundStrategy {

    FIFO("FIFO", "先进先出"),
    FEFO("FEFO", "先到期先出"),
    MANUAL("MANUAL", "手动指定");

    private final String code;
    private final String description;
}
