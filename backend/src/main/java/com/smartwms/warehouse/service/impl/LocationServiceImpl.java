package com.smartwms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.ResultCode;
import com.smartwms.warehouse.entity.Location;
import com.smartwms.warehouse.mapper.LocationMapper;
import com.smartwms.warehouse.service.LocationService;
import com.smartwms.inventory.entity.Inventory;
import com.smartwms.inventory.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location> implements LocationService {

    private final LocationMapper locationMapper;
    private final InventoryMapper inventoryMapper;

    @Override
    public List<Location> listLocations(Long rackId) {
        LambdaQueryWrapper<Location> wrapper = new LambdaQueryWrapper<>();
        if (rackId != null) {
            wrapper.eq(Location::getRackId, rackId);
        }
        wrapper.orderByAsc(Location::getLocationCode);
        return locationMapper.selectList(wrapper);
    }

    @Override
    public Location getLocationById(Long id) {
        Location location = locationMapper.selectById(id);
        if (location == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return location;
    }

    @Override
    @Transactional
    public Location createLocation(Location location) {
        locationMapper.insert(location);
        return location;
    }

    @Override
    @Transactional
    public Location updateLocation(Long id, Location location) {
        Location existing = locationMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        location.setId(id);
        locationMapper.updateById(location);
        return location;
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        if (inventoryMapper.selectCount(new LambdaQueryWrapper<Inventory>().eq(Inventory::getLocationId, id)) > 0)
            throw new BusinessException("库位存在库存，不能删除");
        locationMapper.deleteById(id);
    }
}
