package com.smartwms.inbound.controller;

import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.inbound.dto.InboundOrderCreateRequest;
import com.smartwms.inbound.entity.InboundOrder;
import com.smartwms.inbound.entity.InboundOrderItem;
import com.smartwms.inbound.service.InboundOrderService;
import com.smartwms.inbound.vo.InboundOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "入库管理", description = "入库单CRUD操作")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('inbound:management')")
@RequestMapping("/inbound-orders")
@RequiredArgsConstructor
public class InboundController {

    private final InboundOrderService inboundOrderService;

    @Operation(summary = "分页查询入库单列表")
    @GetMapping
    public R<PageResult<InboundOrderVO>> listInboundOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return R.ok(inboundOrderService.listInboundOrders(page, pageSize, keyword, status));
    }

    @Operation(summary = "获取入库单详情")
    @GetMapping("/{id}")
    public R<InboundOrderVO> getInboundOrderById(@PathVariable Long id) {
        return R.ok(inboundOrderService.getInboundOrderById(id));
    }

    @Operation(summary = "获取入库单明细")
    @GetMapping("/{id}/items")
    public R<List<InboundOrderItem>> getInboundOrderItems(@PathVariable Long id) {
        return R.ok(inboundOrderService.getInboundOrderItems(id));
    }

    @Operation(summary = "创建入库单")
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('inbound:create')")
    public R<InboundOrderVO> createInboundOrder(@RequestBody InboundOrderCreateRequest request) {
        return R.ok(inboundOrderService.createInboundOrder(request.getOrder(), request.getItems()));
    }

    @Operation(summary = "收货确认")
    @PostMapping("/{id}/receive")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('inbound:receive')")
    public R<Void> receiveInboundOrder(@PathVariable Long id) {
        inboundOrderService.receiveInboundOrder(id);
        return R.ok();
    }

    @Operation(summary = "上架确认")
    @PostMapping("/{id}/putaway")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('inbound:putaway')")
    public R<Void> putawayInboundOrder(@PathVariable Long id) {
        inboundOrderService.putawayInboundOrder(id);
        return R.ok();
    }
}
