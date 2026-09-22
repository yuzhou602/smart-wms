package com.smartwms.warehouse.controller;

import com.smartwms.common.response.R;
import com.smartwms.warehouse.entity.Location;
import com.smartwms.warehouse.entity.Rack;
import com.smartwms.warehouse.entity.Zone;
import com.smartwms.warehouse.service.LocationService;
import com.smartwms.warehouse.service.RackService;
import com.smartwms.warehouse.service.ZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "仓库结构管理", description = "库区、货架、库位管理")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('warehouse:center')")
@RequiredArgsConstructor
public class WarehouseStructureController {

    private final ZoneService zoneService;
    private final RackService rackService;
    private final LocationService locationService;

    @Operation(summary = "获取库区列表")
    @GetMapping("/zones")
    public R<List<Zone>> listZones(@RequestParam(required = false) Long warehouseId) {
        return R.ok(zoneService.listZones(warehouseId));
    }

    @Operation(summary = "获取库区详情")
    @GetMapping("/zones/{id}")
    public R<Zone> getZoneById(@PathVariable Long id) {
        return R.ok(zoneService.getZoneById(id));
    }

    @Operation(summary = "创建库区")
    @PostMapping("/zones")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('warehouse:create')")
    public R<Zone> createZone(@RequestBody Zone zone) {
        return R.ok(zoneService.createZone(zone));
    }

    @Operation(summary = "更新库区")
    @PutMapping("/zones/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('warehouse:update')")
    public R<Zone> updateZone(@PathVariable Long id, @RequestBody Zone zone) {
        return R.ok(zoneService.updateZone(id, zone));
    }

    @Operation(summary = "删除库区")
    @DeleteMapping("/zones/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('warehouse:delete')")
    public R<Void> deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return R.ok();
    }

    @Operation(summary = "获取货架列表")
    @GetMapping("/racks")
    public R<List<Rack>> listRacks(@RequestParam(required = false) Long zoneId) {
        return R.ok(rackService.listRacks(zoneId));
    }

    @Operation(summary = "获取货架详情")
    @GetMapping("/racks/{id}")
    public R<Rack> getRackById(@PathVariable Long id) {
        return R.ok(rackService.getRackById(id));
    }

    @Operation(summary = "创建货架")
    @PostMapping("/racks")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('warehouse:create')")
    public R<Rack> createRack(@RequestBody Rack rack) {
        return R.ok(rackService.createRack(rack));
    }

    @Operation(summary = "更新货架")
    @PutMapping("/racks/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('warehouse:update')")
    public R<Rack> updateRack(@PathVariable Long id, @RequestBody Rack rack) {
        return R.ok(rackService.updateRack(id, rack));
    }

    @Operation(summary = "删除货架")
    @DeleteMapping("/racks/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('warehouse:delete')")
    public R<Void> deleteRack(@PathVariable Long id) {
        rackService.deleteRack(id);
        return R.ok();
    }

    @Operation(summary = "获取库位列表")
    @GetMapping("/locations")
    public R<List<Location>> listLocations(@RequestParam(required = false) Long rackId) {
        return R.ok(locationService.listLocations(rackId));
    }

    @Operation(summary = "获取库位详情")
    @GetMapping("/locations/{id}")
    public R<Location> getLocationById(@PathVariable Long id) {
        return R.ok(locationService.getLocationById(id));
    }

    @Operation(summary = "创建库位")
    @PostMapping("/locations")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('warehouse:create')")
    public R<Location> createLocation(@RequestBody Location location) {
        return R.ok(locationService.createLocation(location));
    }

    @Operation(summary = "更新库位")
    @PutMapping("/locations/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('warehouse:update')")
    public R<Location> updateLocation(@PathVariable Long id, @RequestBody Location location) {
        return R.ok(locationService.updateLocation(id, location));
    }

    @Operation(summary = "删除库位")
    @DeleteMapping("/locations/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('warehouse:delete')")
    public R<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return R.ok();
    }
}
