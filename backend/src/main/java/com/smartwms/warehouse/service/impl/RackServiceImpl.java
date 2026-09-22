package com.smartwms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.ResultCode;
import com.smartwms.warehouse.entity.Rack;
import com.smartwms.warehouse.mapper.RackMapper;
import com.smartwms.warehouse.service.RackService;
import com.smartwms.warehouse.entity.Location;
import com.smartwms.warehouse.mapper.LocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RackServiceImpl extends ServiceImpl<RackMapper, Rack> implements RackService {

    private final RackMapper rackMapper;
    private final LocationMapper locationMapper;

    @Override
    public List<Rack> listRacks(Long zoneId) {
        LambdaQueryWrapper<Rack> wrapper = new LambdaQueryWrapper<>();
        if (zoneId != null) {
            wrapper.eq(Rack::getZoneId, zoneId);
        }
        wrapper.orderByAsc(Rack::getRackCode);
        return rackMapper.selectList(wrapper);
    }

    @Override
    public Rack getRackById(Long id) {
        Rack rack = rackMapper.selectById(id);
        if (rack == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return rack;
    }

    @Override
    @Transactional
    public Rack createRack(Rack rack) {
        rackMapper.insert(rack);
        return rack;
    }

    @Override
    @Transactional
    public Rack updateRack(Long id, Rack rack) {
        Rack existing = rackMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        rack.setId(id);
        rackMapper.updateById(rack);
        return rack;
    }

    @Override
    @Transactional
    public void deleteRack(Long id) {
        if (locationMapper.selectCount(new LambdaQueryWrapper<Location>().eq(Location::getRackId, id)) > 0)
            throw new BusinessException("货架下存在库位，不能删除");
        rackMapper.deleteById(id);
    }
}
