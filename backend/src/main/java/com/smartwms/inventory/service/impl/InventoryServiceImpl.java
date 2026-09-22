package com.smartwms.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.ResultCode;
import com.smartwms.inventory.entity.Inventory;
import com.smartwms.inventory.entity.InventoryTransaction;
import com.smartwms.inventory.mapper.InventoryMapper;
import com.smartwms.inventory.mapper.InventoryTransactionMapper;
import com.smartwms.inventory.service.InventoryService;
import com.smartwms.inventory.vo.InventoryVO;
import com.smartwms.warehouse.entity.Location;
import com.smartwms.warehouse.mapper.LocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final InventoryTransactionMapper transactionMapper;
    private final LocationMapper locationMapper;

    @Override
    public PageResult<InventoryVO> listInventory(int page, int pageSize, String keyword, Long warehouseId, String status) {
        Page<Inventory> pageResult = inventoryMapper.selectDetailPage(new Page<>(page, pageSize), keyword, warehouseId, status);

        List<InventoryVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public InventoryVO getInventoryById(Long id) {
        Inventory inventory = inventoryMapper.selectDetailById(id);
        if (inventory == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToVO(inventory);
    }

    @Override
    public List<InventoryTransaction> getInventoryTransactions(Long inventoryId) {
        Inventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        LambdaQueryWrapper<InventoryTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryTransaction::getSkuId, inventory.getSkuId())
                .eq(InventoryTransaction::getWarehouseId, inventory.getWarehouseId())
                .orderByDesc(InventoryTransaction::getCreatedAt);

        return transactionMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void increaseInventory(Long warehouseId, Long locationId, Long skuId, Long batchId, int qty, String sourceOrderNo) {
        Inventory inventory = findInventory(warehouseId, locationId, skuId, batchId);

        int beforeQty = inventory.getTotalQty();
        inventory.setTotalQty(beforeQty + qty);
        inventory.setAvailableQty(inventory.getAvailableQty() + qty);
        inventory.setVersion(inventory.getVersion() + 1);
        inventoryMapper.updateById(inventory);

        saveTransaction(skuId, warehouseId, locationId, batchId, "PURCHASE_IN", beforeQty, qty, inventory.getTotalQty(), sourceOrderNo);
    }

    @Override
    @Transactional
    public void decreaseInventory(Long warehouseId, Long locationId, Long skuId, Long batchId, int qty, String sourceOrderNo) {
        Inventory inventory = findInventory(warehouseId, locationId, skuId, batchId);

        if (inventory.getAvailableQty() < qty) {
            throw new BusinessException(ResultCode.INVENTORY_NOT_ENOUGH);
        }

        int beforeQty = inventory.getTotalQty();
        inventory.setTotalQty(beforeQty - qty);
        inventory.setAvailableQty(inventory.getAvailableQty() - qty);
        inventory.setVersion(inventory.getVersion() + 1);
        inventoryMapper.updateById(inventory);

        saveTransaction(skuId, warehouseId, locationId, batchId, "SALE_OUT", beforeQty, -qty, inventory.getTotalQty(), sourceOrderNo);
    }

    @Override
    @Transactional
    public void lockInventory(Long warehouseId, Long skuId, int qty, String sourceOrderNo) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId)
                .eq(Inventory::getSkuId, skuId);

        List<Inventory> inventories = inventoryMapper.selectList(wrapper);

        int remaining = qty;
        for (Inventory inventory : inventories) {
            if (remaining <= 0) break;

            int available = inventory.getAvailableQty();
            int lockQty = Math.min(available, remaining);

            if (lockQty > 0) {
                inventory.setAvailableQty(available - lockQty);
                inventory.setLockedQty(inventory.getLockedQty() + lockQty);
                inventory.setVersion(inventory.getVersion() + 1);
                inventoryMapper.updateById(inventory);

                saveTransaction(skuId, warehouseId, inventory.getLocationId(), inventory.getBatchId(),
                        "LOCK", available, -lockQty, inventory.getAvailableQty(), sourceOrderNo);

                remaining -= lockQty;
            }
        }

        if (remaining > 0) {
            throw new BusinessException(ResultCode.INVENTORY_NOT_ENOUGH);
        }
    }

    @Override
    @Transactional
    public void unlockInventory(Long warehouseId, Long skuId, int qty, String sourceOrderNo) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId)
                .eq(Inventory::getSkuId, skuId)
                .gt(Inventory::getLockedQty, 0);

        List<Inventory> inventories = inventoryMapper.selectList(wrapper);

        int remaining = qty;
        for (Inventory inventory : inventories) {
            if (remaining <= 0) break;

            int locked = inventory.getLockedQty();
            int unlockQty = Math.min(locked, remaining);

            if (unlockQty > 0) {
                int beforeAvailable = inventory.getAvailableQty();
                inventory.setAvailableQty(inventory.getAvailableQty() + unlockQty);
                inventory.setLockedQty(locked - unlockQty);
                inventory.setVersion(inventory.getVersion() + 1);
                inventoryMapper.updateById(inventory);

                saveTransaction(skuId, warehouseId, inventory.getLocationId(), inventory.getBatchId(),
                        "UNLOCK", beforeAvailable, unlockQty, inventory.getAvailableQty(), sourceOrderNo);

                remaining -= unlockQty;
            }
        }
    }

    @Override
    @Transactional
    public void consumeLockedInventory(Long warehouseId, Long skuId, int qty, String sourceOrderNo) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId)
                .eq(Inventory::getSkuId, skuId)
                .gt(Inventory::getLockedQty, 0)
                .orderByAsc(Inventory::getCreatedAt);
        List<Inventory> inventories = inventoryMapper.selectList(wrapper);
        int totalLocked = inventories.stream().mapToInt(Inventory::getLockedQty).sum();
        if (totalLocked < qty) {
            throw new BusinessException(ResultCode.INVENTORY_NOT_ENOUGH);
        }
        int remaining = qty;
        for (Inventory inventory : inventories) {
            if (remaining <= 0) break;
            int consumeQty = Math.min(inventory.getLockedQty(), remaining);
            int beforeQty = inventory.getTotalQty();
            inventory.setLockedQty(inventory.getLockedQty() - consumeQty);
            inventory.setTotalQty(beforeQty - consumeQty);
            inventory.setVersion(inventory.getVersion() + 1);
            inventoryMapper.updateById(inventory);
            saveTransaction(skuId, warehouseId, inventory.getLocationId(), inventory.getBatchId(),
                    "SALE_OUT", beforeQty, -consumeQty, inventory.getTotalQty(), sourceOrderNo);
            remaining -= consumeQty;
        }
    }

    @Override
    @Transactional
    public void adjustForStocktake(Long warehouseId, Long locationId, Long skuId, Long batchId,
                                   int actualQty, String sourceOrderNo) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId)
                .eq(Inventory::getLocationId, locationId)
                .eq(Inventory::getSkuId, skuId);
        if (batchId == null) wrapper.isNull(Inventory::getBatchId);
        else wrapper.eq(Inventory::getBatchId, batchId);
        Inventory inventory = inventoryMapper.selectOne(wrapper);
        if (inventory == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        if (actualQty < inventory.getLockedQty() + inventory.getDamagedQty()) {
            throw new BusinessException("实盘数量不能小于已锁定与破损库存之和");
        }
        int beforeQty = inventory.getTotalQty();
        inventory.setTotalQty(actualQty);
        inventory.setAvailableQty(actualQty - inventory.getLockedQty() - inventory.getDamagedQty());
        inventory.setVersion(inventory.getVersion() + 1);
        inventoryMapper.updateById(inventory);
        saveTransaction(skuId, warehouseId, locationId, batchId, "STOCKTAKE_ADJUST",
                beforeQty, actualQty - beforeQty, actualQty, sourceOrderNo);
    }

    @Override
    @Transactional
    public void transferInventory(Long sourceWarehouseId, Long targetWarehouseId, Long skuId,
                                  Long batchId, int qty, String sourceOrderNo) {
        Location targetLocation = locationMapper.selectFirstAvailableByWarehouse(targetWarehouseId);
        if (targetLocation == null) throw new BusinessException("目标仓库没有可用库位");
        LambdaQueryWrapper<Inventory> sourceWrapper = new LambdaQueryWrapper<>();
        sourceWrapper.eq(Inventory::getWarehouseId, sourceWarehouseId)
                .eq(Inventory::getSkuId, skuId).gt(Inventory::getAvailableQty, 0)
                .orderByAsc(Inventory::getCreatedAt);
        if (batchId != null) sourceWrapper.eq(Inventory::getBatchId, batchId);
        List<Inventory> sources = inventoryMapper.selectList(sourceWrapper);
        int available = sources.stream().mapToInt(Inventory::getAvailableQty).sum();
        if (available < qty) throw new BusinessException(ResultCode.INVENTORY_NOT_ENOUGH);
        int remaining = qty;
        for (Inventory source : sources) {
            if (remaining <= 0) break;
            int moved = Math.min(source.getAvailableQty(), remaining);
            int sourceBefore = source.getTotalQty();
            source.setAvailableQty(source.getAvailableQty() - moved);
            source.setTotalQty(sourceBefore - moved);
            source.setVersion(source.getVersion() + 1);
            inventoryMapper.updateById(source);
            saveTransaction(skuId, sourceWarehouseId, source.getLocationId(), source.getBatchId(),
                    "TRANSFER_OUT", sourceBefore, -moved, source.getTotalQty(), sourceOrderNo);
            Inventory target = findInventory(targetWarehouseId, targetLocation.getId(), skuId, source.getBatchId());
            int targetBefore = target.getTotalQty();
            target.setTotalQty(targetBefore + moved);
            target.setAvailableQty(target.getAvailableQty() + moved);
            target.setVersion(target.getVersion() + 1);
            inventoryMapper.updateById(target);
            saveTransaction(skuId, targetWarehouseId, targetLocation.getId(), source.getBatchId(),
                    "TRANSFER_IN", targetBefore, moved, target.getTotalQty(), sourceOrderNo);
            remaining -= moved;
        }
    }

    private Inventory findInventory(Long warehouseId, Long locationId, Long skuId, Long batchId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId)
                .eq(Inventory::getLocationId, locationId)
                .eq(Inventory::getSkuId, skuId)
                .eq(Inventory::getBatchId, batchId);

        Inventory inventory = inventoryMapper.selectOne(wrapper);
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setWarehouseId(warehouseId);
            inventory.setLocationId(locationId);
            inventory.setSkuId(skuId);
            inventory.setBatchId(batchId);
            inventory.setTotalQty(0);
            inventory.setAvailableQty(0);
            inventory.setLockedQty(0);
            inventory.setDamagedQty(0);
            inventory.setVersion(0);
            inventoryMapper.insert(inventory);
        }
        return inventory;
    }

    private void saveTransaction(Long skuId, Long warehouseId, Long locationId, Long batchId,
                                 String type, int beforeQty, int changeQty, int afterQty, String sourceOrderNo) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setTransactionNo("TXN" + System.currentTimeMillis());
        transaction.setSkuId(skuId);
        transaction.setWarehouseId(warehouseId);
        transaction.setLocationId(locationId);
        transaction.setBatchId(batchId);
        transaction.setTransactionType(type);
        transaction.setBeforeQty(beforeQty);
        transaction.setChangeQty(changeQty);
        transaction.setAfterQty(afterQty);
        transaction.setSourceOrderNo(sourceOrderNo);
        transactionMapper.insert(transaction);
    }

    private InventoryVO convertToVO(Inventory inventory) {
        InventoryVO vo = new InventoryVO();
        vo.setId(inventory.getId());
        vo.setWarehouseId(inventory.getWarehouseId());
        vo.setWarehouseName(inventory.getWarehouseName());
        vo.setLocationId(inventory.getLocationId());
        vo.setLocationCode(inventory.getLocationCode());
        vo.setSkuId(inventory.getSkuId());
        vo.setSkuCode(inventory.getSkuCode());
        vo.setProductName(inventory.getProductName());
        vo.setBatchId(inventory.getBatchId());
        vo.setBatchNo(inventory.getBatchNo());
        vo.setTotalQty(inventory.getTotalQty());
        vo.setAvailableQty(inventory.getAvailableQty());
        vo.setLockedQty(inventory.getLockedQty());
        vo.setDamagedQty(inventory.getDamagedQty());
        if (inventory.getDamagedQty() != null && inventory.getDamagedQty() > 0) vo.setStatus("DAMAGED");
        else if (inventory.getAvailableQty() != null && inventory.getAvailableQty() > 0) vo.setStatus("AVAILABLE");
        else if (inventory.getLockedQty() != null && inventory.getLockedQty() > 0) vo.setStatus("LOCKED");
        else vo.setStatus("EMPTY");
        vo.setCreatedAt(inventory.getCreatedAt());
        return vo;
    }
}
