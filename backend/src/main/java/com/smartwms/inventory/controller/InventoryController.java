package com.smartwms.inventory.controller;

import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.inventory.entity.InventoryTransaction;
import com.smartwms.inventory.service.InventoryService;
import com.smartwms.inventory.vo.InventoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "库存管理", description = "库存查询和操作")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('inventory:center')")
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "分页查询库存列表")
    @GetMapping
    public R<PageResult<InventoryVO>> listInventory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String status) {
        return R.ok(inventoryService.listInventory(page, pageSize, keyword, warehouseId, status));
    }

    @Operation(summary = "获取库存详情")
    @GetMapping("/{id}")
    public R<InventoryVO> getInventoryById(@PathVariable Long id) {
        return R.ok(inventoryService.getInventoryById(id));
    }

    @Operation(summary = "获取库存流水")
    @GetMapping("/{id}/transactions")
    public R<List<InventoryTransaction>> getInventoryTransactions(@PathVariable Long id) {
        return R.ok(inventoryService.getInventoryTransactions(id));
    }
}
