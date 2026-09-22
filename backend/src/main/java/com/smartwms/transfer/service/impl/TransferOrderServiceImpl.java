package com.smartwms.transfer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.utils.OrderNumberGenerator;
import com.smartwms.security.SecurityUtils;
import com.smartwms.transfer.entity.TransferOrder;
import com.smartwms.transfer.entity.TransferOrderItem;
import com.smartwms.transfer.mapper.TransferOrderItemMapper;
import com.smartwms.transfer.mapper.TransferOrderMapper;
import com.smartwms.transfer.service.TransferOrderService;
import com.smartwms.transfer.vo.TransferOrderVO;
import com.smartwms.inventory.service.InventoryService;
import com.smartwms.product.mapper.SKUMapper;
import com.smartwms.warehouse.entity.Warehouse;
import com.smartwms.warehouse.mapper.WarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferOrderServiceImpl extends ServiceImpl<TransferOrderMapper, TransferOrder> implements TransferOrderService {

    private final TransferOrderMapper transferOrderMapper;
    private final TransferOrderItemMapper transferOrderItemMapper;
    private final InventoryService inventoryService;
    private final WarehouseMapper warehouseMapper;
    private final SKUMapper skuMapper;

    @Override
    public PageResult<TransferOrderVO> listTransferOrders(int page, int pageSize, String keyword, String status) {
        LambdaQueryWrapper<TransferOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(TransferOrder::getOrderNo, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(TransferOrder::getStatus, status);
        }
        wrapper.orderByDesc(TransferOrder::getCreatedAt);

        Page<TransferOrder> pageResult = transferOrderMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<TransferOrderVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public TransferOrderVO getTransferOrderById(Long id) {
        TransferOrder order = transferOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("调拨单不存在");
        }
        return convertToVO(order);
    }

    @Override
    public List<TransferOrderItem> getTransferOrderItems(Long orderId) {
        getTransferOrderById(orderId);
        return transferOrderItemMapper.selectDetailsByOrderId(orderId);
    }

    @Override
    @Transactional
    public TransferOrderVO createTransferOrder(TransferOrder order, List<TransferOrderItem> items) {
        if (order == null || order.getSourceWarehouseId() == null || order.getTargetWarehouseId() == null) {
            throw new BusinessException("源仓库和目标仓库不能为空");
        }
        if (order.getSourceWarehouseId().equals(order.getTargetWarehouseId())) {
            throw new BusinessException("源仓库和目标仓库不能相同");
        }
        if (warehouseMapper.selectById(order.getSourceWarehouseId()) == null
                || warehouseMapper.selectById(order.getTargetWarehouseId()) == null) {
            throw new BusinessException("调拨仓库不存在或已停用");
        }
        if (items == null || items.isEmpty()) throw new BusinessException("调拨单至少需要一条商品明细");
        for (TransferOrderItem item : items) {
            if (item.getSkuId() == null || skuMapper.selectById(item.getSkuId()) == null)
                throw new BusinessException("调拨商品SKU不存在");
            if (item.getQty() == null || item.getQty() <= 0) throw new BusinessException("调拨数量必须大于0");
        }
        order.setOrderNo(OrderNumberGenerator.generateTransferOrder());
        order.setStatus("CREATED");
        order.setTotalQty(items.stream().mapToInt(TransferOrderItem::getQty).sum());
        order.setShippedQty(0);
        order.setReceivedQty(0);
        order.setCreatedBy(SecurityUtils.getCurrentUserId());
        transferOrderMapper.insert(order);

        for (TransferOrderItem item : items) {
            item.setOrderId(order.getId());
            item.setShippedQty(0);
            item.setReceivedQty(0);
            item.setStatus("PENDING");
            transferOrderItemMapper.insert(item);
        }

        return convertToVO(order);
    }

    @Override
    @Transactional
    public void approveTransferOrder(Long orderId) {
        TransferOrder order = transferOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException("调拨单状态不允许审批");
        }

        order.setStatus("APPROVED");
        order.setApprovedBy(SecurityUtils.getCurrentUserId());
        transferOrderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void executeTransferOrder(Long orderId) {
        TransferOrder order = transferOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (!"APPROVED".equals(order.getStatus())) {
            throw new BusinessException("调拨单状态不允许执行");
        }

        List<TransferOrderItem> items = getTransferOrderItems(orderId);
        int transferred = 0;
        for (TransferOrderItem item : items) {
            inventoryService.transferInventory(order.getSourceWarehouseId(), order.getTargetWarehouseId(),
                    item.getSkuId(), item.getBatchId(), item.getQty(), order.getOrderNo());
            item.setShippedQty(item.getQty());
            item.setReceivedQty(item.getQty());
            item.setStatus("COMPLETED");
            transferOrderItemMapper.updateById(item);
            transferred += item.getQty();
        }
        order.setShippedQty(transferred);
        order.setReceivedQty(transferred);
        order.setStatus("COMPLETED");
        transferOrderMapper.updateById(order);
    }

    private TransferOrderVO convertToVO(TransferOrder order) {
        TransferOrderVO vo = new TransferOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setSourceWarehouseId(order.getSourceWarehouseId());
        Warehouse source = warehouseMapper.selectById(order.getSourceWarehouseId());
        vo.setSourceWarehouseName(source == null ? null : source.getWarehouseName());
        vo.setTargetWarehouseId(order.getTargetWarehouseId());
        Warehouse target = warehouseMapper.selectById(order.getTargetWarehouseId());
        vo.setTargetWarehouseName(target == null ? null : target.getWarehouseName());
        vo.setStatus(order.getStatus());
        vo.setTotalQty(order.getTotalQty());
        vo.setShippedQty(order.getShippedQty());
        vo.setReceivedQty(order.getReceivedQty());
        vo.setRemark(order.getRemark());
        vo.setCreatedAt(order.getCreatedAt());
        return vo;
    }
}
