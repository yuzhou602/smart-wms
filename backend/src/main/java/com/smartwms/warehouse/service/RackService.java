package com.smartwms.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.warehouse.entity.Rack;

import java.util.List;

public interface RackService extends IService<Rack> {

    List<Rack> listRacks(Long zoneId);

    Rack getRackById(Long id);

    Rack createRack(Rack rack);

    Rack updateRack(Long id, Rack rack);

    void deleteRack(Long id);
}
