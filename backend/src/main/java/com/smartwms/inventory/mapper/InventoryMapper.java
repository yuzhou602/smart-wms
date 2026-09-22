package com.smartwms.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartwms.inventory.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    @Select("""
            <script>
            SELECT i.*, w.warehouse_name, l.location_code, s.sku_code, p.product_name, b.batch_no
            FROM wms_inventory i
            LEFT JOIN wms_warehouse w ON i.warehouse_id = w.id
            LEFT JOIN wms_location l ON i.location_id = l.id
            LEFT JOIN wms_sku s ON i.sku_id = s.id
            LEFT JOIN wms_product p ON s.product_id = p.id
            LEFT JOIN wms_batch b ON i.batch_id = b.id
            WHERE 1 = 1
            <if test="warehouseId != null">AND i.warehouse_id = #{warehouseId}</if>
            <if test="keyword != null and keyword != ''">
                AND (s.sku_code LIKE CONCAT('%', #{keyword}, '%') OR p.product_name LIKE CONCAT('%', #{keyword}, '%') OR b.batch_no LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="status == 'AVAILABLE'">AND i.available_qty &gt; 0</if>
            <if test="status == 'LOCKED'">AND i.locked_qty &gt; 0</if>
            <if test="status == 'DAMAGED'">AND i.damaged_qty &gt; 0</if>
            <if test="status == 'EMPTY'">AND i.total_qty = 0</if>
            ORDER BY i.updated_at DESC
            </script>
            """)
    Page<Inventory> selectDetailPage(Page<Inventory> page, @Param("keyword") String keyword,
                                     @Param("warehouseId") Long warehouseId, @Param("status") String status);

    @Select("""
            SELECT i.*, w.warehouse_name, l.location_code, s.sku_code, p.product_name, b.batch_no
            FROM wms_inventory i
            LEFT JOIN wms_warehouse w ON i.warehouse_id = w.id
            LEFT JOIN wms_location l ON i.location_id = l.id
            LEFT JOIN wms_sku s ON i.sku_id = s.id
            LEFT JOIN wms_product p ON s.product_id = p.id
            LEFT JOIN wms_batch b ON i.batch_id = b.id
            WHERE i.id = #{id}
            """)
    Inventory selectDetailById(@Param("id") Long id);

    @Select("""
            SELECT i.*, w.warehouse_name, l.location_code, s.sku_code, p.product_name, b.batch_no
            FROM wms_inventory i
            LEFT JOIN wms_warehouse w ON i.warehouse_id = w.id
            LEFT JOIN wms_location l ON i.location_id = l.id
            LEFT JOIN wms_sku s ON i.sku_id = s.id
            LEFT JOIN wms_product p ON s.product_id = p.id
            LEFT JOIN wms_batch b ON i.batch_id = b.id
            WHERE i.warehouse_id = #{warehouseId} AND i.sku_id = #{skuId}
            """)
    List<Inventory> selectByWarehouseAndSku(@Param("warehouseId") Long warehouseId, @Param("skuId") Long skuId);
}
