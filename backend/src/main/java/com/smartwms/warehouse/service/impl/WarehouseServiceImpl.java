package com.smartwms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.ResultCode;
import com.smartwms.warehouse.entity.Warehouse;
import com.smartwms.warehouse.mapper.WarehouseMapper;
import com.smartwms.warehouse.service.WarehouseService;
import com.smartwms.warehouse.vo.WarehouseVO;
import com.smartwms.warehouse.entity.Zone;
import com.smartwms.warehouse.mapper.ZoneMapper;
import com.smartwms.inventory.entity.Inventory;
import com.smartwms.inventory.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    private final WarehouseMapper warehouseMapper;
    private final ZoneMapper zoneMapper;
    private final InventoryMapper inventoryMapper;

    @Override
    public PageResult<WarehouseVO> listWarehouses(int page, int pageSize, String keyword) {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Warehouse::getWarehouseCode, keyword)
                    .or().like(Warehouse::getWarehouseName, keyword);
        }
        wrapper.orderByDesc(Warehouse::getCreatedAt);

        Page<Warehouse> pageResult = warehouseMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<WarehouseVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public WarehouseVO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToVO(warehouse);
    }

    @Override
    @Transactional
    public WarehouseVO createWarehouse(Warehouse warehouse) {
        validateWarehouse(warehouse, null);
        warehouse.setUsedCapacity(warehouse.getUsedCapacity() == null ? 0 : warehouse.getUsedCapacity());
        warehouse.setStatus(warehouse.getStatus() == null ? 1 : warehouse.getStatus());
        warehouseMapper.insert(warehouse);
        return convertToVO(warehouse);
    }

    @Override
    @Transactional
    public WarehouseVO updateWarehouse(Long id, Warehouse warehouse) {
        Warehouse existing = warehouseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        validateWarehouse(warehouse, id);
        warehouse.setId(id);
        warehouseMapper.updateById(warehouse);
        return convertToVO(warehouse);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (zoneMapper.selectCount(new LambdaQueryWrapper<Zone>().eq(Zone::getWarehouseId, id)) > 0
                || inventoryMapper.selectCount(new LambdaQueryWrapper<Inventory>().eq(Inventory::getWarehouseId, id)) > 0) {
            throw new BusinessException("仓库存在库区或库存，不能删除");
        }
        warehouseMapper.deleteById(id);
    }

    private WarehouseVO convertToVO(Warehouse warehouse) {
        WarehouseVO vo = new WarehouseVO();
        vo.setId(warehouse.getId());
        vo.setWarehouseCode(warehouse.getWarehouseCode());
        vo.setWarehouseName(warehouse.getWarehouseName());
        vo.setAddress(warehouse.getAddress());
        vo.setContactPerson(warehouse.getContactPerson());
        vo.setPhone(warehouse.getPhone());
        vo.setTotalCapacity(warehouse.getTotalCapacity());
        vo.setUsedCapacity(warehouse.getUsedCapacity());
        vo.setStatus(warehouse.getStatus());
        vo.setCreatedAt(warehouse.getCreatedAt());
        return vo;
    }

    private void validateWarehouse(Warehouse warehouse, Long currentId) {
        if (warehouse == null || !StringUtils.hasText(warehouse.getWarehouseCode())
                || !StringUtils.hasText(warehouse.getWarehouseName())) {
            throw new BusinessException("仓库编码和名称不能为空");
        }
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<Warehouse>()
                .eq(Warehouse::getWarehouseCode, warehouse.getWarehouseCode());
        if (currentId != null) wrapper.ne(Warehouse::getId, currentId);
        if (warehouseMapper.selectCount(wrapper) > 0) throw new BusinessException("仓库编码已存在");
        if (warehouse.getTotalCapacity() != null && warehouse.getTotalCapacity() < 0)
            throw new BusinessException("仓库容量不能小于0");
    }
}
