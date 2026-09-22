package com.smartwms.analytics.controller;

import com.smartwms.analytics.service.AnalyticsService;
import com.smartwms.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Tag(name="数据分析",description="真实仓储运营分析") @RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('analytics')")
@RequestMapping("/analytics") @RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    @Operation(summary="获取库存、ABC、周转率和出库排行")
    @GetMapping public R<Map<String,Object>> getAnalytics(@RequestParam(defaultValue="30") int days){return R.ok(analyticsService.getAnalytics(days));}
}
