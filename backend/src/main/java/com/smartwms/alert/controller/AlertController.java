package com.smartwms.alert.controller;

import com.smartwms.alert.entity.Alert;
import com.smartwms.alert.service.AlertService;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "预警管理", description = "库存预警CRUD操作")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('alert:center')")
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @Operation(summary = "获取预警汇总")
    @GetMapping("/summary")
    public R<java.util.Map<String, Long>> getSummary() {
        return R.ok(alertService.getSummary());
    }

    @Operation(summary = "分页查询预警列表")
    @GetMapping
    public R<PageResult<Alert>> listAlerts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer isHandled,
            @RequestParam(required = false) String alertType) {
        return R.ok(alertService.listAlerts(page, pageSize, isHandled, alertType));
    }

    @Operation(summary = "获取预警详情")
    @GetMapping("/{id}")
    public R<Alert> getAlertById(@PathVariable Long id) {
        return R.ok(alertService.getAlertById(id));
    }

    @Operation(summary = "处理预警")
    @PostMapping("/{id}/handle")
    public R<Void> handleAlert(@PathVariable Long id, @RequestBody HandleRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        alertService.handleAlert(id, userId, request.getHandleResult());
        return R.ok();
    }

    @Data
    public static class HandleRequest {
        private String handleResult;
    }
}
