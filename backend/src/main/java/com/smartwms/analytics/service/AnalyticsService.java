package com.smartwms.analytics.service;

import java.util.Map;

public interface AnalyticsService {
    Map<String,Object> getAnalytics(int days);
    Map<String,Object> getInboundOutboundTrend();
    Map<String,Object> getWarehouseUtilization();
}
