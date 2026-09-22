package com.smartwms.inbound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.utils.OrderNumberGenerator;
import com.smartwms.inbound.entity.InboundOrder;
import com.smartwms.inbound.entity.InboundOrderItem;
import com.smartwms.inbound.mapper.InboundOrderItemMapper;
import com.smartwms.inbound.mapper.InboundOrderMapper;
import com.smartwms.inbound.service.InboundOrderService;
import com.smartwms.inbound.vo.InboundOrderVO;
import com.smartwms.inventory.service.InventoryService;
import com.smartwms.product.entity.Supplier;
import com.smartwms.product.mapper.SKUMapper;
import com.smartwms.product.mapper.SupplierMapper;
import com.smartwms.warehouse.entity.Warehouse;
import com.smartwms.warehouse.mapper.WarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InboundOrderServiceImpl extends ServiceImpl<InboundOrderMapper, InboundOrder> implements InboundOrderService {

    private final InboundOrderMapper inboundOrderMapper;
    private final InboundOrderItemMapper inboundOrderItemMapper;
    private final InventoryService inventoryService;
    private final WarehouseMapper warehouseMapper;
    private final SupplierMapper supplierMapper;
    private final SKUMapper skuMapper;

    @Override
    public PageResult<InboundOrderVO> listInboundOrders(int page, int pageSize, String keyword, String status) {
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(InboundOrder::getOrderNo, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(InboundOrder::getStatus, status);
        }
        wrapper.orderByDesc(InboundOrder::getCreatedAt);

        Page<InboundOrder> pageResult = inboundOrderMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<InboundOrderVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public InboundOrderVO getInboundOrderById(Long id) {
        InboundOrder order = inboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("入库单不存在");
        }
        return convertToVO(order);
    }

    @Override
    public List<InboundOrderItem> getInboundOrderItems(Long orderId) {
        getInboundOrderById(orderId);
        return inboundOrderItemMapper.selectDetailsByOrderId(orderId);
    }

    @Override
    @Transactional
    public InboundOrderVO createInboundOrder(InboundOrder order, List<InboundOrderItem> items) {
        if (order == null || order.getWarehouseId() == null || !StringUtils.hasText(order.getOrderType())) {
            throw new BusinessException("入库类型和仓库不能为空");
        }
        if (warehouseMapper.selectById(order.getWarehouseId()) == null) {
            throw new BusinessException("入库仓库不存在或已停用");
        }
        if (items == null || items.isEmpty()) {
            throw new BusinessException("入库单至少需要一条商品明细");
        }
        for (InboundOrderItem item : items) {
            if (item.getSkuId() == null || skuMapper.selectById(item.getSkuId()) == null) {
                throw new BusinessException("入库商品SKU不存在");
            }
            if (item.getExpectedQty() == null || item.getExpectedQty() <= 0) {
                throw new BusinessException("入库数量必须大于0");
            }
        }
        order.setOrderNo(OrderNumberGenerator.generateInboundOrder());
        order.setStatus("CREATED");
        order.setTotalQty(items.stream().mapToInt(InboundOrderItem::getExpectedQty).sum());
        order.setReceivedQty(0);
        order.setPutawayQty(0);
        inboundOrderMapper.insert(order);

        for (InboundOrderItem item : items) {
            item.setOrderId(order.getId());
            item.setReceivedQty(0);
            item.setPutawayQty(0);
            item.setStatus("PENDING");
            inboundOrderItemMapper.insert(item);
        }

        return convertToVO(order);
    }

    @Override
    @Transactional
    public void receiveInboundOrder(Long orderId) {
        InboundOrder order = inboundOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("入库单不存在");
        }
        if (!"CREATED".equals(order.getStatus()) && !"RECEIVING".equals(order.getStatus())) {
            throw new BusinessException("入库单状态不允许收货");
        }

        // 获取入库单明细
        List<InboundOrderItem> items = getInboundOrderItems(orderId);

        // 更新收货数量为预期数量
        int totalReceivedQty = 0;
        for (InboundOrderItem item : items) {
            item.setReceivedQty(item.getExpectedQty());
            item.setStatus("RECEIVED");
            inboundOrderItemMapper.updateById(item);
            totalReceivedQty += item.getReceivedQty();
        }

        order.setReceivedQty(totalReceivedQty);
        order.setStatus("RECEIVED");
        inboundOrderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void putawayInboundOrder(Long orderId) {
        InboundOrder order = inboundOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("入库单不存在");
        }
        if (!"RECEIVED".equals(order.getStatus())) {
            throw new BusinessException("入库单状态不允许上架");
        }

        // 获取入库单明细
        List<InboundOrderItem> items = getInboundOrderItems(orderId);

        // 增加库存
        for (InboundOrderItem item : items) {
            inventoryService.increaseInventory(
                order.getWarehouseId(),
                null,
                item.getSkuId(),
                null,
                item.getReceivedQty(),
                order.getOrderNo()
            );

            item.setPutawayQty(item.getReceivedQty());
            item.setStatus("PUTAWAY");
            inboundOrderItemMapper.updateById(item);
        }

        order.setPutawayQty(order.getReceivedQty());
        order.setStatus("PUTAWAY");
        inboundOrderMapper.updateById(order);
    }

    private InboundOrderVO convertToVO(InboundOrder order) {
        InboundOrderVO vo = new InboundOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setOrderType(order.getOrderType());
        vo.setWarehouseId(order.getWarehouseId());
        Warehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
        vo.setWarehouseName(warehouse == null ? null : warehouse.getWarehouseName());
        vo.setSupplierId(order.getSupplierId());
        Supplier supplier = order.getSupplierId() == null ? null : supplierMapper.selectById(order.getSupplierId());
        vo.setSupplierName(supplier == null ? null : supplier.getSupplierName());
        vo.setStatus(order.getStatus());
        vo.setTotalQty(order.getTotalQty());
        vo.setReceivedQty(order.getReceivedQty());
        vo.setPutawayQty(order.getPutawayQty());
        vo.setExpectedDate(order.getExpectedDate());
        vo.setCreatedAt(order.getCreatedAt());
        return vo;
    }
}
