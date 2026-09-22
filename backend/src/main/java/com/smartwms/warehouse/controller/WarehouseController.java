package com.smartwms.warehouse.controller;

import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.warehouse.entity.Warehouse;
import com.smartwms.warehouse.service.WarehouseService;
import com.smartwms.warehouse.vo.WarehouseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "仓库管理", description = "仓库CRUD操作")
@RestController
@PreAuthorize("hasAuthority('warehouse:center')")
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Operation(summary = "分页查询仓库列表")
    @GetMapping
    public R<PageResult<WarehouseVO>> listWarehouses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        return R.ok(warehouseService.listWarehouses(page, pageSize, keyword));
    }

    @Operation(summary = "获取仓库详情")
    @GetMapping("/{id}")
    public R<WarehouseVO> getWarehouseById(@PathVariable Long id) {
        return R.ok(warehouseService.getWarehouseById(id));
    }

    @Operation(summary = "创建仓库")
    @PostMapping
    @PreAuthorize("hasAuthority('warehouse:create')")
    public R<WarehouseVO> createWarehouse(@RequestBody Warehouse warehouse) {
        return R.ok(warehouseService.createWarehouse(warehouse));
    }

    @Operation(summary = "更新仓库")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse:update')")
    public R<WarehouseVO> updateWarehouse(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        return R.ok(warehouseService.updateWarehouse(id, warehouse));
    }

    @Operation(summary = "删除仓库")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse:delete')")
    public R<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return R.ok();
    }
}
