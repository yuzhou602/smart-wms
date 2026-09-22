package com.smartwms.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartwms.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT COUNT(*) FROM wms_sku WHERE product_id = #{productId} AND deleted = 0")
    int countSkusByProductId(@Param("productId") Long productId);

    @Select("SELECT category_name FROM wms_category WHERE id = #{categoryId}")
    String getCategoryNameById(@Param("categoryId") Long categoryId);
}
