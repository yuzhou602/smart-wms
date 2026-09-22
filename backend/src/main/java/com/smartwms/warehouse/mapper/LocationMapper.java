package com.smartwms.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartwms.warehouse.entity.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LocationMapper extends BaseMapper<Location> {
    @Select("""
            SELECT l.* FROM wms_location l
            WHERE l.warehouse_id = #{warehouseId} AND l.status IN ('EMPTY', 'AVAILABLE', 'PARTIAL')
            ORDER BY l.id LIMIT 1
            """)
    Location selectFirstAvailableByWarehouse(@Param("warehouseId") Long warehouseId);
}
