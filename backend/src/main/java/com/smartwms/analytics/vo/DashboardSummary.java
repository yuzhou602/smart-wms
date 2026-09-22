package com.smartwms.analytics.vo;

import lombok.Data;

@Data
public class DashboardSummary {

    private Long totalSku;

    private Long totalProduct;

    private Long totalInventory;

    private Long todayInbound;

    private Long todayOutbound;

    private Long pendingTasks;

    private Long alerts;
}
