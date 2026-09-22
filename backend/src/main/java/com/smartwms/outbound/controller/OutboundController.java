package com.smartwms.outbound.controller;

import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.outbound.dto.OutboundOrderCreateRequest;
import com.smartwms.outbound.entity.OutboundOrder;
import com.smartwms.outbound.entity.OutboundOrderItem;
import com.smartwms.outbound.service.OutboundOrderService;
import com.smartwms.outbound.vo.OutboundOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "出库管理", description = "出库单CRUD操作")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('outbound:management')")
@RequestMapping("/outbound-orders")
@RequiredArgsConstructor
public class OutboundController {

    private final OutboundOrderService outboundOrderService;

    @Operation(summary = "分页查询出库单列表")
    @GetMapping
    public R<PageResult<OutboundOrderVO>> listOutboundOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return R.ok(outboundOrderService.listOutboundOrders(page, pageSize, keyword, status));
    }

    @Operation(summary = "获取出库单详情")
    @GetMapping("/{id}")
    public R<OutboundOrderVO> getOutboundOrderById(@PathVariable Long id) {
        return R.ok(outboundOrderService.getOutboundOrderById(id));
    }

    @Operation(summary = "获取出库单明细")
    @GetMapping("/{id}/items")
    public R<List<OutboundOrderItem>> getOutboundOrderItems(@PathVariable Long id) {
        return R.ok(outboundOrderService.getOutboundOrderItems(id));
    }

    @Operation(summary = "创建出库单")
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('outbound:create')")
    public R<OutboundOrderVO> createOutboundOrder(@RequestBody OutboundOrderCreateRequest request) {
        return R.ok(outboundOrderService.createOutboundOrder(request.getOrder(), request.getItems()));
    }

    @Operation(summary = "审核出库")
    @PostMapping("/{id}/approve")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('outbound:approve')")
    public R<Void> approveOutboundOrder(@PathVariable Long id) {
        outboundOrderService.approveOutboundOrder(id);
        return R.ok();
    }

    @Operation(summary = "开始拣货")
    @PostMapping("/{id}/pick")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('outbound:pick')")
    public R<Void> pickOutboundOrder(@PathVariable Long id) {
        outboundOrderService.pickOutboundOrder(id);
        return R.ok();
    }

    @Operation(summary = "发货确认")
    @PostMapping("/{id}/ship")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('outbound:ship')")
    public R<Void> shipOutboundOrder(@PathVariable Long id) {
        outboundOrderService.shipOutboundOrder(id);
        return R.ok();
    }
}
