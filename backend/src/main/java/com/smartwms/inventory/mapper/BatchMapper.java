package com.smartwms.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartwms.inventory.entity.Batch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BatchMapper extends BaseMapper<Batch> {
    @Select("""
            <script>
            SELECT b.*, s.sku_code, p.product_name, sp.supplier_name
            FROM wms_batch b
            LEFT JOIN wms_sku s ON b.sku_id = s.id
            LEFT JOIN wms_product p ON s.product_id = p.id
            LEFT JOIN wms_supplier sp ON b.supplier_id = sp.id
            WHERE b.deleted = 0
            <if test="keyword != null and keyword != ''">
              AND (b.batch_no LIKE CONCAT('%', #{keyword}, '%') OR s.sku_code LIKE CONCAT('%', #{keyword}, '%') OR p.product_name LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="qualityStatus != null and qualityStatus != ''">AND b.quality_status = #{qualityStatus}</if>
            <if test="expiryBefore != null">AND b.expiry_date &lt;= #{expiryBefore}</if>
            ORDER BY b.expiry_date ASC, b.created_at DESC
            </script>
            """)
    Page<Batch> selectDetailPage(Page<Batch> page, @Param("keyword") String keyword,
                                 @Param("qualityStatus") String qualityStatus,
                                 @Param("expiryBefore") java.time.LocalDate expiryBefore);
}
