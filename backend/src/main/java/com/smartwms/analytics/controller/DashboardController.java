package com.smartwms.analytics.controller;

import com.smartwms.analytics.service.DashboardService;
import com.smartwms.analytics.service.AnalyticsService;
import com.smartwms.analytics.vo.DashboardSummary;
import com.smartwms.alert.entity.Alert;
import com.smartwms.alert.service.AlertService;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.task.entity.Task;
import com.smartwms.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "数据看板", description = "Dashboard数据统计")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('dashboard')")
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final TaskService taskService;
    private final AlertService alertService;
    private final AnalyticsService analyticsService;

    @Operation(summary = "获取Dashboard汇总数据")
    @GetMapping("/summary")
    public R<DashboardSummary> getSummary() {
        return R.ok(dashboardService.getSummary());
    }

    @Operation(summary = "获取今日任务")
    @GetMapping("/tasks")
    public R<List<Task>> getTodayTasks() {
        PageResult<Task> result = taskService.listTasks(1, 10, null, "IN_PROGRESS", null, null);
        return R.ok(result.getRecords());
    }

    @Operation(summary = "获取库存预警")
    @GetMapping("/alerts")
    public R<List<Alert>> getAlerts() {
        PageResult<Alert> result = alertService.listAlerts(1, 10, 0, null);
        return R.ok(result.getRecords());
    }

    @Operation(summary = "获取出入库趋势")
    @GetMapping("/inbound-outbound-trend")
    public R<Map<String, Object>> getInboundOutboundTrend() {
        return R.ok(analyticsService.getInboundOutboundTrend());
    }

    @Operation(summary = "获取仓库利用率")
    @GetMapping("/utilization")
    public R<Map<String, Object>> getUtilization() {
        return R.ok(analyticsService.getWarehouseUtilization());
    }
}
