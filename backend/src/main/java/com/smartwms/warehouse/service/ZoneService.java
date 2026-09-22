package com.smartwms.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.warehouse.entity.Zone;

import java.util.List;

public interface ZoneService extends IService<Zone> {

    List<Zone> listZones(Long warehouseId);

    Zone getZoneById(Long id);

    Zone createZone(Zone zone);

    Zone updateZone(Long id, Zone zone);

    void deleteZone(Long id);
}
