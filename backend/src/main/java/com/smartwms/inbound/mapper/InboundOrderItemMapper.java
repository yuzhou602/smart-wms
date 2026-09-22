package com.smartwms.inbound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartwms.inbound.entity.InboundOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InboundOrderItemMapper extends BaseMapper<InboundOrderItem> {

    @Select("""
            SELECT i.*, s.sku_code, p.product_name
            FROM wms_inbound_order_item i
            LEFT JOIN wms_sku s ON i.sku_id = s.id
            LEFT JOIN wms_product p ON s.product_id = p.id
            WHERE i.order_id = #{orderId}
            ORDER BY i.created_at ASC
            """)
    List<InboundOrderItem> selectDetailsByOrderId(@Param("orderId") Long orderId);
}
