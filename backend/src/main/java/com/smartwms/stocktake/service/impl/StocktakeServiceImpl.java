package com.smartwms.stocktake.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.ResultCode;
import com.smartwms.common.utils.OrderNumberGenerator;
import com.smartwms.stocktake.entity.Stocktake;
import com.smartwms.stocktake.entity.StocktakeItem;
import com.smartwms.stocktake.mapper.StocktakeItemMapper;
import com.smartwms.stocktake.mapper.StocktakeMapper;
import com.smartwms.stocktake.service.StocktakeService;
import com.smartwms.stocktake.vo.StocktakeVO;
import com.smartwms.inventory.entity.Inventory;
import com.smartwms.inventory.mapper.InventoryMapper;
import com.smartwms.inventory.service.InventoryService;
import com.smartwms.warehouse.entity.Warehouse;
import com.smartwms.warehouse.mapper.WarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StocktakeServiceImpl extends ServiceImpl<StocktakeMapper, Stocktake> implements StocktakeService {

    private final StocktakeMapper stocktakeMapper;
    private final StocktakeItemMapper stocktakeItemMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryService inventoryService;
    private final WarehouseMapper warehouseMapper;

    @Override
    public PageResult<StocktakeVO> listStocktakes(int page, int pageSize, String keyword, String status) {
        LambdaQueryWrapper<Stocktake> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Stocktake::getStocktakeNo, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Stocktake::getStatus, status);
        }
        wrapper.orderByDesc(Stocktake::getCreatedAt);

        Page<Stocktake> pageResult = stocktakeMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<StocktakeVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public StocktakeVO getStocktakeById(Long id) {
        Stocktake stocktake = stocktakeMapper.selectById(id);
        if (stocktake == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(stocktake);
    }

    @Override
    public List<StocktakeItem> getStocktakeItems(Long stocktakeId) {
        getStocktakeById(stocktakeId);
        return stocktakeItemMapper.selectDetailsByStocktakeId(stocktakeId);
    }

    @Override
    @Transactional
    public StocktakeVO createStocktake(Stocktake stocktake, List<StocktakeItem> items) {
        if (stocktake == null || stocktake.getWarehouseId() == null) {
            throw new BusinessException("盘点仓库不能为空");
        }
        if (warehouseMapper.selectById(stocktake.getWarehouseId()) == null) {
            throw new BusinessException("盘点仓库不存在或已停用");
        }
        if (!"FULL".equals(stocktake.getStocktakeType()) && !"SAMPLE".equals(stocktake.getStocktakeType())) {
            throw new BusinessException("盘点类型不正确");
        }
        LambdaQueryWrapper<Inventory> inventoryWrapper = new LambdaQueryWrapper<>();
        inventoryWrapper.eq(Inventory::getWarehouseId, stocktake.getWarehouseId())
                .gt(Inventory::getTotalQty, 0);
        List<Inventory> warehouseInventories = inventoryMapper.selectList(inventoryWrapper);
        List<StocktakeItem> snapshotItems = new ArrayList<>();
        if ("FULL".equals(stocktake.getStocktakeType())) {
            for (Inventory inventory : warehouseInventories) snapshotItems.add(toSnapshotItem(inventory));
        } else {
            if (items == null || items.isEmpty()) throw new BusinessException("抽盘至少需要选择一条库存");
            for (StocktakeItem requested : items) {
                Inventory inventory = warehouseInventories.stream()
                        .filter(it -> it.getSkuId().equals(requested.getSkuId())
                                && java.util.Objects.equals(it.getLocationId(), requested.getLocationId())
                                && java.util.Objects.equals(it.getBatchId(), requested.getBatchId()))
                        .findFirst().orElseThrow(() -> new BusinessException("抽盘库存不属于所选仓库"));
                snapshotItems.add(toSnapshotItem(inventory));
            }
        }
        if (snapshotItems.isEmpty()) throw new BusinessException("所选仓库暂无可盘点库存");
        stocktake.setStocktakeNo(OrderNumberGenerator.generateStocktake());
        stocktake.setStatus("CREATED");
        stocktake.setTotalItems(snapshotItems.size());
        stocktake.setCountedItems(0);
        stocktake.setDiffItems(0);
        stocktakeMapper.insert(stocktake);

        for (StocktakeItem item : snapshotItems) {
            item.setStocktakeId(stocktake.getId());
            item.setStatus("PENDING");
            stocktakeItemMapper.insert(item);
        }

        return convertToVO(stocktake);
    }

    @Override
    @Transactional
    public void startStocktake(Long id) {
        Stocktake stocktake = stocktakeMapper.selectById(id);
        if (stocktake == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!"CREATED".equals(stocktake.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        stocktake.setStatus("IN_PROGRESS");
        stocktakeMapper.updateById(stocktake);
    }

    @Override
    @Transactional
    public void countStocktakeItem(Long itemId, Integer actualQty, Long userId) {
        StocktakeItem item = stocktakeItemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (actualQty == null || actualQty < 0) throw new BusinessException("实盘数量不能小于0");
        Stocktake stocktake = stocktakeMapper.selectById(item.getStocktakeId());
        if (stocktake == null || !"IN_PROGRESS".equals(stocktake.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        item.setActualQty(actualQty);
        item.setDiffQty(actualQty - item.getSystemQty());
        item.setStatus("COUNTED");
        stocktakeItemMapper.updateById(item);
        LambdaQueryWrapper<StocktakeItem> countedWrapper = new LambdaQueryWrapper<>();
        countedWrapper.eq(StocktakeItem::getStocktakeId, stocktake.getId()).eq(StocktakeItem::getStatus, "COUNTED");
        stocktake.setCountedItems(Math.toIntExact(stocktakeItemMapper.selectCount(countedWrapper)));
        LambdaQueryWrapper<StocktakeItem> diffWrapper = new LambdaQueryWrapper<>();
        diffWrapper.eq(StocktakeItem::getStocktakeId, stocktake.getId()).ne(StocktakeItem::getDiffQty, 0);
        stocktake.setDiffItems(Math.toIntExact(stocktakeItemMapper.selectCount(diffWrapper)));
        stocktakeMapper.updateById(stocktake);
    }

    @Override
    @Transactional
    public void completeStocktake(Long id) {
        Stocktake stocktake = stocktakeMapper.selectById(id);
        if (stocktake == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!"IN_PROGRESS".equals(stocktake.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        List<StocktakeItem> items = getStocktakeItems(id);
        if (items.stream().anyMatch(item -> !"COUNTED".equals(item.getStatus()))) {
            throw new BusinessException("仍有未盘点的商品，不能完成盘点");
        }
        for (StocktakeItem item : items) {
            if (item.getDiffQty() != null && item.getDiffQty() != 0) {
                inventoryService.adjustForStocktake(stocktake.getWarehouseId(), item.getLocationId(),
                        item.getSkuId(), item.getBatchId(), item.getActualQty(), stocktake.getStocktakeNo());
                item.setStatus("ADJUSTED");
                stocktakeItemMapper.updateById(item);
            }
        }

        stocktake.setStatus("COMPLETED");
        stocktakeMapper.updateById(stocktake);
    }

    private StocktakeVO convertToVO(Stocktake stocktake) {
        StocktakeVO vo = new StocktakeVO();
        vo.setId(stocktake.getId());
        vo.setStocktakeNo(stocktake.getStocktakeNo());
        vo.setStocktakeType(stocktake.getStocktakeType());
        vo.setWarehouseId(stocktake.getWarehouseId());
        Warehouse warehouse = warehouseMapper.selectById(stocktake.getWarehouseId());
        vo.setWarehouseName(warehouse == null ? null : warehouse.getWarehouseName());
        vo.setStatus(stocktake.getStatus());
        vo.setTotalItems(stocktake.getTotalItems());
        vo.setCountedItems(stocktake.getCountedItems());
        vo.setDiffItems(stocktake.getDiffItems());
        vo.setCreatedAt(stocktake.getCreatedAt());
        return vo;
    }

    private StocktakeItem toSnapshotItem(Inventory inventory) {
        StocktakeItem item = new StocktakeItem();
        item.setSkuId(inventory.getSkuId());
        item.setLocationId(inventory.getLocationId());
        item.setBatchId(inventory.getBatchId());
        item.setSystemQty(inventory.getTotalQty());
        item.setDiffQty(0);
        return item;
    }
}
