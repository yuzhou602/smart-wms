package com.smartwms.stocktake.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartwms.stocktake.entity.StocktakeItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StocktakeItemMapper extends BaseMapper<StocktakeItem> {
    @Select("""
            SELECT i.*, s.sku_code, p.product_name, l.location_code, b.batch_no
            FROM wms_stocktake_item i
            LEFT JOIN wms_sku s ON i.sku_id = s.id
            LEFT JOIN wms_product p ON s.product_id = p.id
            LEFT JOIN wms_location l ON i.location_id = l.id
            LEFT JOIN wms_batch b ON i.batch_id = b.id
            WHERE i.stocktake_id = #{stocktakeId}
            ORDER BY l.location_code, s.sku_code
            """)
    List<StocktakeItem> selectDetailsByStocktakeId(@Param("stocktakeId") Long stocktakeId);
}
