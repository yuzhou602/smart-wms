package com.smartwms.analytics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartwms.analytics.service.DashboardService;
import com.smartwms.analytics.vo.DashboardSummary;
import com.smartwms.inventory.entity.Inventory;
import com.smartwms.inventory.mapper.InventoryMapper;
import com.smartwms.inbound.entity.InboundOrder;
import com.smartwms.inbound.mapper.InboundOrderMapper;
import com.smartwms.outbound.entity.OutboundOrder;
import com.smartwms.outbound.mapper.OutboundOrderMapper;
import com.smartwms.product.mapper.ProductMapper;
import com.smartwms.product.mapper.SKUMapper;
import com.smartwms.alert.entity.Alert;
import com.smartwms.alert.mapper.AlertMapper;
import com.smartwms.task.entity.Task;
import com.smartwms.task.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductMapper productMapper;
    private final SKUMapper skuMapper;
    private final InventoryMapper inventoryMapper;
    private final InboundOrderMapper inboundOrderMapper;
    private final OutboundOrderMapper outboundOrderMapper;
    private final TaskMapper taskMapper;
    private final AlertMapper alertMapper;

    @Override
    public DashboardSummary getSummary() {
        DashboardSummary summary = new DashboardSummary();

        summary.setTotalSku(skuMapper.selectCount(null));
        summary.setTotalProduct(productMapper.selectCount(null));

        LambdaQueryWrapper<Inventory> inventoryWrapper = new LambdaQueryWrapper<>();
        summary.setTotalInventory(inventoryMapper.selectList(inventoryWrapper).stream()
                .mapToLong(item -> item.getTotalQty() == null ? 0 : item.getTotalQty()).sum());

        LambdaQueryWrapper<InboundOrder> todayInboundWrapper = new LambdaQueryWrapper<>();
        todayInboundWrapper.ge(InboundOrder::getCreatedAt, LocalDate.now().atStartOfDay());
        summary.setTodayInbound(inboundOrderMapper.selectList(todayInboundWrapper).stream()
                .mapToLong(order -> order.getPutawayQty() == null ? 0 : order.getPutawayQty()).sum());

        LambdaQueryWrapper<OutboundOrder> todayOutboundWrapper = new LambdaQueryWrapper<>();
        todayOutboundWrapper.ge(OutboundOrder::getCreatedAt, LocalDate.now().atStartOfDay());
        summary.setTodayOutbound(outboundOrderMapper.selectList(todayOutboundWrapper).stream()
                .mapToLong(order -> order.getShippedQty() == null ? 0 : order.getShippedQty()).sum());

        LambdaQueryWrapper<Task> pendingTaskWrapper = new LambdaQueryWrapper<>();
        pendingTaskWrapper.in(Task::getStatus, "PENDING", "IN_PROGRESS");
        summary.setPendingTasks(taskMapper.selectCount(pendingTaskWrapper));

        LambdaQueryWrapper<Alert> alertWrapper = new LambdaQueryWrapper<>();
        alertWrapper.eq(Alert::getIsHandled, 0);
        summary.setAlerts(alertMapper.selectCount(alertWrapper));

        return summary;
    }
}
