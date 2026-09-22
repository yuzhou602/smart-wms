package com.smartwms.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.R;
import com.smartwms.product.entity.Product;
import com.smartwms.product.entity.SKU;
import com.smartwms.product.mapper.ProductMapper;
import com.smartwms.product.mapper.SKUMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SKU管理", description = "商品SKU的查询与维护")
@RestController
@PreAuthorize("hasAuthority('product:center')")
@RequiredArgsConstructor
public class SKUController {
    private final SKUMapper skuMapper;
    private final ProductMapper productMapper;

    @Operation(summary = "查询商品SKU")
    @GetMapping("/products/{productId}/skus")
    public R<List<SKU>> listByProduct(@PathVariable Long productId) {
        requireProduct(productId);
        return R.ok(skuMapper.selectList(new LambdaQueryWrapper<SKU>()
                .eq(SKU::getProductId, productId).orderByAsc(SKU::getSkuCode)));
    }

    @Operation(summary = "创建SKU")
    @PostMapping("/skus")
    @PreAuthorize("hasAuthority('product:create')")
    public R<SKU> create(@RequestBody SKU sku) {
        validate(sku, null);
        sku.setId(null);
        if (sku.getStatus() == null) sku.setStatus(1);
        if (!StringUtils.hasText(sku.getOutboundStrategy())) sku.setOutboundStrategy("FIFO");
        skuMapper.insert(sku);
        return R.ok(sku);
    }

    @Operation(summary = "更新SKU")
    @PutMapping("/skus/{id}")
    @PreAuthorize("hasAuthority('product:update')")
    public R<SKU> update(@PathVariable Long id, @RequestBody SKU sku) {
        if (skuMapper.selectById(id) == null) throw new BusinessException("SKU不存在");
        validate(sku, id);
        sku.setId(id);
        skuMapper.updateById(sku);
        return R.ok(skuMapper.selectById(id));
    }

    @Operation(summary = "删除SKU")
    @DeleteMapping("/skus/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    @Transactional
    public R<Void> delete(@PathVariable Long id) {
        if (skuMapper.selectById(id) == null) throw new BusinessException("SKU不存在");
        if (skuMapper.countInventory(id) > 0) throw new BusinessException("SKU仍有库存，不能删除");
        skuMapper.deleteById(id);
        return R.ok();
    }

    private void validate(SKU sku, Long excludedId) {
        if (sku.getProductId() == null || !StringUtils.hasText(sku.getSkuCode()))
            throw new BusinessException("商品和SKU编码不能为空");
        requireProduct(sku.getProductId());
        LambdaQueryWrapper<SKU> query = new LambdaQueryWrapper<SKU>().eq(SKU::getSkuCode, sku.getSkuCode());
        if (excludedId != null) query.ne(SKU::getId, excludedId);
        if (skuMapper.selectCount(query) > 0) throw new BusinessException("SKU编码已存在");
    }

    private Product requireProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new BusinessException("商品不存在");
        return product;
    }
}
