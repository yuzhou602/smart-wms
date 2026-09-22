package com.smartwms.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.inventory.entity.Batch;
import com.smartwms.inventory.mapper.BatchMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "批次管理", description = "批次、质检及效期查询")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('batch:management')")
@RequestMapping("/batches")
@RequiredArgsConstructor
public class BatchController {
    private final BatchMapper batchMapper;

    @Operation(summary = "分页查询批次")
    @GetMapping
    public R<PageResult<Batch>> list(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "20") int pageSize,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String qualityStatus,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryBefore) {
        Page<Batch> result = batchMapper.selectDetailPage(new Page<>(page, pageSize), keyword, qualityStatus, expiryBefore);
        return R.ok(PageResult.of(result.getRecords(), result.getTotal(), page, pageSize));
    }
}
