package com.smartwms.transfer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartwms.transfer.entity.TransferOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TransferOrderItemMapper extends BaseMapper<TransferOrderItem> {
    @Select("""
            SELECT i.*, s.sku_code, p.product_name, b.batch_no
            FROM wms_transfer_order_item i
            LEFT JOIN wms_sku s ON i.sku_id = s.id
            LEFT JOIN wms_product p ON s.product_id = p.id
            LEFT JOIN wms_batch b ON i.batch_id = b.id
            WHERE i.order_id = #{orderId}
            ORDER BY i.created_at, i.id
            """)
    List<TransferOrderItem> selectDetailsByOrderId(@Param("orderId") Long orderId);
}
