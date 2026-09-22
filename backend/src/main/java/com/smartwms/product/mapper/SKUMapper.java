package com.smartwms.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartwms.product.entity.SKU;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SKUMapper extends BaseMapper<SKU> {

    @Select("""
            SELECT s.*, p.product_name
            FROM wms_sku s
            LEFT JOIN wms_product p ON s.product_id = p.id
            WHERE s.deleted = 0 AND s.status = 1
            ORDER BY s.sku_code
            """)
    List<SKU> selectActiveWithProduct();

    @Select("SELECT COUNT(*) FROM wms_inventory WHERE sku_id = #{skuId} AND total_qty > 0")
    long countInventory(@Param("skuId") Long skuId);
}
