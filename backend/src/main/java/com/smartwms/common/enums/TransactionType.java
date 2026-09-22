package com.smartwms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {

    PURCHASE_IN("PURCHASE_IN", "采购入库"),
    PRODUCTION_IN("PRODUCTION_IN", "生产入库"),
    RETURN_IN("RETURN_IN", "退货入库"),
    TRANSFER_IN("TRANSFER_IN", "调拨入库"),
    OTHER_IN("OTHER_IN", "其他入库"),

    SALE_OUT("SALE_OUT", "销售出库"),
    MATERIAL_OUT("MATERIAL_OUT", "生产领料"),
    TRANSFER_OUT("TRANSFER_OUT", "调拨出库"),
    RETURN_OUT("RETURN_OUT", "退货出库"),
    OTHER_OUT("OTHER_OUT", "其他出库"),

    STOCKTAKE_INCREASE("STOCKTAKE_INCREASE", "盘点盘盈"),
    STOCKTAKE_DECREASE("STOCKTAKE_DECREASE", "盘点盘亏"),

    LOCK("LOCK", "库存锁定"),
    UNLOCK("UNLOCK", "库存释放");

    private final String code;
    private final String description;
}
