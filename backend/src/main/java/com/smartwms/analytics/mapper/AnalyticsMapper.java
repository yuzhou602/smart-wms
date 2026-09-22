package com.smartwms.analytics.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface AnalyticsMapper {
    @Select("SELECT DATE(created_at) day, COALESCE(SUM(change_qty),0) changeQty FROM wms_inventory_transaction WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) GROUP BY DATE(created_at) ORDER BY day")
    List<Map<String,Object>> dailyInventoryChanges(@Param("days") int days);

    @Select("""
        SELECT s.id skuId, s.sku_code skuCode, p.product_name productName,
               COALESCE(SUM(i.shipped_qty),0) quantity
        FROM wms_outbound_order_item i JOIN wms_outbound_order o ON i.order_id=o.id
        JOIN wms_sku s ON i.sku_id=s.id JOIN wms_product p ON s.product_id=p.id
        WHERE o.status='COMPLETED' GROUP BY s.id,s.sku_code,p.product_name ORDER BY quantity DESC
        """)
    List<Map<String,Object>> outboundBySku();

    @Select("""
        SELECT DATE_FORMAT(o.created_at,'%Y-%m') month, COALESCE(SUM(o.shipped_qty),0) quantity
        FROM wms_outbound_order o WHERE o.status='COMPLETED'
          AND o.created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
        GROUP BY DATE_FORMAT(o.created_at,'%Y-%m') ORDER BY month
        """)
    List<Map<String,Object>> monthlyOutbound();

    @Select("""
        SELECT DATE(created_at) day, COALESCE(SUM(putaway_qty),0) quantity
        FROM wms_inbound_order WHERE status IN ('PUTAWAY','COMPLETED')
          AND created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
        GROUP BY DATE(created_at) ORDER BY day
        """)
    List<Map<String,Object>> dailyInbound();

    @Select("""
        SELECT DATE(created_at) day, COALESCE(SUM(shipped_qty),0) quantity
        FROM wms_outbound_order WHERE status='COMPLETED'
          AND created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
        GROUP BY DATE(created_at) ORDER BY day
        """)
    List<Map<String,Object>> dailyOutbound();

    @Select("""
        SELECT w.id, w.warehouse_name name, COALESCE(SUM(l.used_capacity),0) usedCapacity,
               COALESCE(SUM(l.max_capacity),0) totalCapacity
        FROM wms_warehouse w LEFT JOIN wms_location l ON l.warehouse_id=w.id AND l.deleted=0
        WHERE w.deleted=0 GROUP BY w.id,w.warehouse_name ORDER BY w.warehouse_code
        """)
    List<Map<String,Object>> warehouseUtilization();
}
