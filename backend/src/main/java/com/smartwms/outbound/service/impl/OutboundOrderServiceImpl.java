package com.smartwms.outbound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.utils.OrderNumberGenerator;
import com.smartwms.inventory.service.InventoryService;
import com.smartwms.outbound.entity.OutboundOrder;
import com.smartwms.outbound.entity.OutboundOrderItem;
import com.smartwms.outbound.mapper.OutboundOrderItemMapper;
import com.smartwms.outbound.mapper.OutboundOrderMapper;
import com.smartwms.outbound.service.OutboundOrderService;
import com.smartwms.outbound.vo.OutboundOrderVO;
import com.smartwms.warehouse.entity.Warehouse;
import com.smartwms.warehouse.mapper.WarehouseMapper;
import com.smartwms.product.mapper.SKUMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboundOrderServiceImpl extends ServiceImpl<OutboundOrderMapper, OutboundOrder> implements OutboundOrderService {

    private final OutboundOrderMapper outboundOrderMapper;
    private final OutboundOrderItemMapper outboundOrderItemMapper;
    private final InventoryService inventoryService;
    private final WarehouseMapper warehouseMapper;
    private final SKUMapper skuMapper;

    @Override
    public PageResult<OutboundOrderVO> listOutboundOrders(int page, int pageSize, String keyword, String status) {
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(OutboundOrder::getOrderNo, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(OutboundOrder::getStatus, status);
        }
        wrapper.orderByDesc(OutboundOrder::getCreatedAt);

        Page<OutboundOrder> pageResult = outboundOrderMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<OutboundOrderVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public OutboundOrderVO getOutboundOrderById(Long id) {
        OutboundOrder order = outboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("出库单不存在");
        }
        return convertToVO(order);
    }

    @Override
    public List<OutboundOrderItem> getOutboundOrderItems(Long orderId) {
        getOutboundOrderById(orderId);
        return outboundOrderItemMapper.selectDetailsByOrderId(orderId);
    }

    @Override
    @Transactional
    public OutboundOrderVO createOutboundOrder(OutboundOrder order, List<OutboundOrderItem> items) {
        if (order == null || order.getWarehouseId() == null || !StringUtils.hasText(order.getOrderType())) {
            throw new BusinessException("出库类型和仓库不能为空");
        }
        if (!StringUtils.hasText(order.getCustomerName())) {
            throw new BusinessException("客户或领用部门不能为空");
        }
        if (warehouseMapper.selectById(order.getWarehouseId()) == null) {
            throw new BusinessException("出库仓库不存在或已停用");
        }
        if (items == null || items.isEmpty()) {
            throw new BusinessException("出库单至少需要一条商品明细");
        }
        for (OutboundOrderItem item : items) {
            if (item.getSkuId() == null || skuMapper.selectById(item.getSkuId()) == null) {
                throw new BusinessException("出库商品SKU不存在");
            }
            if (item.getRequiredQty() == null || item.getRequiredQty() <= 0) {
                throw new BusinessException("出库数量必须大于0");
            }
        }
        order.setOrderNo(OrderNumberGenerator.generateOutboundOrder());
        order.setStatus("CREATED");
        order.setTotalQty(items.stream().mapToInt(OutboundOrderItem::getRequiredQty).sum());
        order.setPickedQty(0);
        order.setShippedQty(0);
        outboundOrderMapper.insert(order);

        for (OutboundOrderItem item : items) {
            item.setOrderId(order.getId());
            item.setAllocatedQty(0);
            item.setPickedQty(0);
            item.setLockedQty(0);
            item.setStatus("PENDING");
            outboundOrderItemMapper.insert(item);
        }

        return convertToVO(order);
    }

    @Override
    @Transactional
    public void approveOutboundOrder(Long orderId) {
        OutboundOrder order = outboundOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("出库单不存在");
        }
        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException("出库单状态不允许审核");
        }

        order.setStatus("APPROVED");
        outboundOrderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void pickOutboundOrder(Long orderId) {
        OutboundOrder order = outboundOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("出库单不存在");
        }
        if (!"APPROVED".equals(order.getStatus())) {
            throw new BusinessException("出库单状态不允许拣货");
        }

        // 获取出库单明细
        List<OutboundOrderItem> items = getOutboundOrderItems(orderId);

        // 锁定库存
        for (OutboundOrderItem item : items) {
            inventoryService.lockInventory(
                order.getWarehouseId(),
                item.getSkuId(),
                item.getRequiredQty(),
                order.getOrderNo()
            );

            item.setLockedQty(item.getRequiredQty());
            item.setStatus("PICKING");
            outboundOrderItemMapper.updateById(item);
        }

        order.setStatus("PICKING");
        outboundOrderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void shipOutboundOrder(Long orderId) {
        OutboundOrder order = outboundOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("出库单不存在");
        }
        if (!"PICKING".equals(order.getStatus()) && !"CHECKING".equals(order.getStatus())) {
            throw new BusinessException("出库单状态不允许发货");
        }

        // 获取出库单明细
        List<OutboundOrderItem> items = getOutboundOrderItems(orderId);

        // 扣减库存
        int totalShippedQty = 0;
        for (OutboundOrderItem item : items) {
            inventoryService.consumeLockedInventory(order.getWarehouseId(), item.getSkuId(),
                    item.getRequiredQty(), order.getOrderNo());

            item.setPickedQty(item.getRequiredQty());
            item.setStatus("SHIPPED");
            outboundOrderItemMapper.updateById(item);
            totalShippedQty += item.getPickedQty();
        }

        order.setPickedQty(totalShippedQty);
        order.setShippedQty(totalShippedQty);
        order.setStatus("COMPLETED");
        outboundOrderMapper.updateById(order);
    }

    private OutboundOrderVO convertToVO(OutboundOrder order) {
        OutboundOrderVO vo = new OutboundOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setOrderType(order.getOrderType());
        vo.setWarehouseId(order.getWarehouseId());
        Warehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
        vo.setWarehouseName(warehouse == null ? null : warehouse.getWarehouseName());
        vo.setCustomerName(order.getCustomerName());
        vo.setStatus(order.getStatus());
        vo.setTotalQty(order.getTotalQty());
        vo.setPickedQty(order.getPickedQty());
        vo.setShippedQty(order.getShippedQty());
        vo.setExpectedDate(order.getExpectedDate());
        vo.setCreatedAt(order.getCreatedAt());
        return vo;
    }
}
