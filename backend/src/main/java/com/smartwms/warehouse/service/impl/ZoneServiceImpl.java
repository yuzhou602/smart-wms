package com.smartwms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.ResultCode;
import com.smartwms.warehouse.entity.Zone;
import com.smartwms.warehouse.mapper.ZoneMapper;
import com.smartwms.warehouse.service.ZoneService;
import com.smartwms.warehouse.entity.Rack;
import com.smartwms.warehouse.mapper.RackMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl extends ServiceImpl<ZoneMapper, Zone> implements ZoneService {

    private final ZoneMapper zoneMapper;
    private final RackMapper rackMapper;

    @Override
    public List<Zone> listZones(Long warehouseId) {
        LambdaQueryWrapper<Zone> wrapper = new LambdaQueryWrapper<>();
        if (warehouseId != null) {
            wrapper.eq(Zone::getWarehouseId, warehouseId);
        }
        wrapper.orderByAsc(Zone::getZoneCode);
        return zoneMapper.selectList(wrapper);
    }

    @Override
    public Zone getZoneById(Long id) {
        Zone zone = zoneMapper.selectById(id);
        if (zone == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return zone;
    }

    @Override
    @Transactional
    public Zone createZone(Zone zone) {
        zoneMapper.insert(zone);
        return zone;
    }

    @Override
    @Transactional
    public Zone updateZone(Long id, Zone zone) {
        Zone existing = zoneMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        zone.setId(id);
        zoneMapper.updateById(zone);
        return zone;
    }

    @Override
    @Transactional
    public void deleteZone(Long id) {
        if (rackMapper.selectCount(new LambdaQueryWrapper<Rack>().eq(Rack::getZoneId, id)) > 0)
            throw new BusinessException("库区下存在货架，不能删除");
        zoneMapper.deleteById(id);
    }
}
