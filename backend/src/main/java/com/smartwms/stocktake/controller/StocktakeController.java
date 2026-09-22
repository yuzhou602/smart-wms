package com.smartwms.stocktake.controller;

import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.security.SecurityUtils;
import com.smartwms.stocktake.dto.StocktakeCreateRequest;
import com.smartwms.stocktake.entity.Stocktake;
import com.smartwms.stocktake.entity.StocktakeItem;
import com.smartwms.stocktake.service.StocktakeService;
import com.smartwms.stocktake.vo.StocktakeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "盘点管理", description = "盘点单CRUD操作")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('stocktake:management')")
@RequestMapping("/stocktakes")
@RequiredArgsConstructor
public class StocktakeController {

    private final StocktakeService stocktakeService;

    @Operation(summary = "分页查询盘点单列表")
    @GetMapping
    public R<PageResult<StocktakeVO>> listStocktakes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return R.ok(stocktakeService.listStocktakes(page, pageSize, keyword, status));
    }

    @Operation(summary = "获取盘点单详情")
    @GetMapping("/{id}")
    public R<StocktakeVO> getStocktakeById(@PathVariable Long id) {
        return R.ok(stocktakeService.getStocktakeById(id));
    }

    @Operation(summary = "获取盘点单明细")
    @GetMapping("/{id}/items")
    public R<List<StocktakeItem>> getStocktakeItems(@PathVariable Long id) {
        return R.ok(stocktakeService.getStocktakeItems(id));
    }

    @Operation(summary = "创建盘点单")
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('stocktake:create')")
    public R<StocktakeVO> createStocktake(@RequestBody StocktakeCreateRequest request) {
        return R.ok(stocktakeService.createStocktake(request.getStocktake(), request.getItems()));
    }

    @Operation(summary = "开始盘点")
    @PostMapping("/{id}/start")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('stocktake:approve')")
    public R<Void> startStocktake(@PathVariable Long id) {
        stocktakeService.startStocktake(id);
        return R.ok();
    }

    @Operation(summary = "录入盘点结果")
    @PostMapping("/items/{itemId}/count")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('stocktake:adjust')")
    public R<Void> countStocktakeItem(@PathVariable Long itemId, @RequestParam Integer actualQty) {
        Long userId = SecurityUtils.getCurrentUserId();
        stocktakeService.countStocktakeItem(itemId, actualQty, userId);
        return R.ok();
    }

    @Operation(summary = "完成盘点")
    @PostMapping("/{id}/complete")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('stocktake:adjust')")
    public R<Void> completeStocktake(@PathVariable Long id) {
        stocktakeService.completeStocktake(id);
        return R.ok();
    }
}
