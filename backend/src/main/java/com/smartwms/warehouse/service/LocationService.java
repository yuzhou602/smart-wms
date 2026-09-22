package com.smartwms.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.warehouse.entity.Location;

import java.util.List;

public interface LocationService extends IService<Location> {

    List<Location> listLocations(Long rackId);

    Location getLocationById(Long id);

    Location createLocation(Location location);

    Location updateLocation(Long id, Location location);

    void deleteLocation(Long id);
}
