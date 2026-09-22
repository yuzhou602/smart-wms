package com.smartwms.system.log.controller;

import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.system.log.entity.OperationLog;
import com.smartwms.system.log.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Tag(name = "操作日志", description = "查询系统操作日志")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('operation:log')")
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {

    private final OperationLogService operationLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping
    public R<PageResult<OperationLog>> listLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return R.ok(operationLogService.listLogs(page, pageSize, keyword, module, startTime, endTime));
    }
}
