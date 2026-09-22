package com.smartwms.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.common.response.PageResult;
import com.smartwms.inventory.entity.Inventory;
import com.smartwms.inventory.entity.InventoryTransaction;
import com.smartwms.inventory.vo.InventoryVO;

import java.util.List;

public interface InventoryService extends IService<Inventory> {

    PageResult<InventoryVO> listInventory(int page, int pageSize, String keyword, Long warehouseId, String status);

    InventoryVO getInventoryById(Long id);

    List<InventoryTransaction> getInventoryTransactions(Long inventoryId);

    void increaseInventory(Long warehouseId, Long locationId, Long skuId, Long batchId, int qty, String sourceOrderNo);

    void decreaseInventory(Long warehouseId, Long locationId, Long skuId, Long batchId, int qty, String sourceOrderNo);

    void lockInventory(Long warehouseId, Long skuId, int qty, String sourceOrderNo);

    void unlockInventory(Long warehouseId, Long skuId, int qty, String sourceOrderNo);

    void consumeLockedInventory(Long warehouseId, Long skuId, int qty, String sourceOrderNo);

    void adjustForStocktake(Long warehouseId, Long locationId, Long skuId, Long batchId,
                            int actualQty, String sourceOrderNo);

    void transferInventory(Long sourceWarehouseId, Long targetWarehouseId, Long skuId,
                           Long batchId, int qty, String sourceOrderNo);
}
