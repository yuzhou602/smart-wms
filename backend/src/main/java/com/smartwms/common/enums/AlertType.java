package com.smartwms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertType {

    LOW_STOCK("LOW_STOCK", "低库存预警"),
    OVERSTOCK("OVERSTOCK", "超储预警"),
    EXPIRING("EXPIRING", "临期预警"),
    SLOW_MOVING("SLOW_MOVING", "呆滞库存"),
    LOCATION_CAPACITY("LOCATION_CAPACITY", "库位容量预警");

    private final String code;
    private final String description;
}
