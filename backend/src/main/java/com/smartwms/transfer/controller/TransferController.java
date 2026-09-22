package com.smartwms.transfer.controller;

import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.transfer.dto.TransferOrderCreateRequest;
import com.smartwms.transfer.entity.TransferOrder;
import com.smartwms.transfer.entity.TransferOrderItem;
import com.smartwms.transfer.service.TransferOrderService;
import com.smartwms.transfer.vo.TransferOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "调拨管理", description = "调拨单CRUD操作")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('transfer:management')")
@RequestMapping("/transfer-orders")
@RequiredArgsConstructor
public class TransferController {

    private final TransferOrderService transferOrderService;

    @Operation(summary = "分页查询调拨单列表")
    @GetMapping
    public R<PageResult<TransferOrderVO>> listTransferOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return R.ok(transferOrderService.listTransferOrders(page, pageSize, keyword, status));
    }

    @Operation(summary = "获取调拨单详情")
    @GetMapping("/{id}")
    public R<TransferOrderVO> getTransferOrderById(@PathVariable Long id) {
        return R.ok(transferOrderService.getTransferOrderById(id));
    }

    @Operation(summary = "获取调拨单明细")
    @GetMapping("/{id}/items")
    public R<List<TransferOrderItem>> getTransferOrderItems(@PathVariable Long id) {
        return R.ok(transferOrderService.getTransferOrderItems(id));
    }

    @Operation(summary = "创建调拨单")
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('transfer:create')")
    public R<TransferOrderVO> createTransferOrder(@RequestBody TransferOrderCreateRequest request) {
        return R.ok(transferOrderService.createTransferOrder(request.getOrder(), request.getItems()));
    }

    @Operation(summary = "审核调拨")
    @PostMapping("/{id}/approve")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('transfer:approve')")
    public R<Void> approveTransferOrder(@PathVariable Long id) {
        transferOrderService.approveTransferOrder(id);
        return R.ok();
    }

    @Operation(summary = "执行调拨")
    @PostMapping("/{id}/execute")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('transfer:approve')")
    public R<Void> executeTransferOrder(@PathVariable Long id) {
        transferOrderService.executeTransferOrder(id);
        return R.ok();
    }
}
