package com.smartwms.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartwms.common.response.R;
import com.smartwms.product.entity.SKU;
import com.smartwms.product.entity.Supplier;
import com.smartwms.product.entity.Category;
import com.smartwms.product.mapper.CategoryMapper;
import com.smartwms.product.mapper.SKUMapper;
import com.smartwms.product.mapper.SupplierMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "商品基础数据", description = "业务表单使用的SKU和供应商选项")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('product:center')")
@RequestMapping("/product-references")
@RequiredArgsConstructor
public class ProductReferenceController {
    private final SKUMapper skuMapper;
    private final SupplierMapper supplierMapper;
    private final CategoryMapper categoryMapper;

    @Operation(summary = "查询启用的SKU")
    @GetMapping("/skus")
    public R<List<SKU>> listSkus() {
        return R.ok(skuMapper.selectActiveWithProduct());
    }

    @Operation(summary = "查询启用的供应商")
    @GetMapping("/suppliers")
    public R<List<Supplier>> listSuppliers() {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getStatus, 1).orderByAsc(Supplier::getSupplierCode);
        return R.ok(supplierMapper.selectList(wrapper));
    }

    @Operation(summary = "查询启用的商品分类")
    @GetMapping("/categories")
    public R<List<Category>> listCategories() {
        return R.ok(categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1).orderByAsc(Category::getSortOrder)));
    }
}
