package com.smartwms.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.common.response.PageResult;
import com.smartwms.warehouse.entity.Warehouse;
import com.smartwms.warehouse.vo.WarehouseVO;

public interface WarehouseService extends IService<Warehouse> {

    PageResult<WarehouseVO> listWarehouses(int page, int pageSize, String keyword);

    WarehouseVO getWarehouseById(Long id);

    WarehouseVO createWarehouse(Warehouse warehouse);

    WarehouseVO updateWarehouse(Long id, Warehouse warehouse);

    void deleteWarehouse(Long id);
}
