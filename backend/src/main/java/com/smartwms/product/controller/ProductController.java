package com.smartwms.product.controller;

import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.product.entity.Product;
import com.smartwms.product.service.ProductService;
import com.smartwms.product.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品管理", description = "商品CRUD操作")
@RestController
@PreAuthorize("hasAuthority('product:center')")
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "分页查询商品列表")
    @GetMapping
    public R<PageResult<ProductVO>> listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        return R.ok(productService.listProducts(page, pageSize, keyword, categoryId, status));
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public R<ProductVO> getProductById(@PathVariable Long id) {
        return R.ok(productService.getProductById(id));
    }

    @Operation(summary = "创建商品")
    @PostMapping
    @PreAuthorize("hasAuthority('product:create')")
    public R<ProductVO> createProduct(@RequestBody Product product) {
        return R.ok(productService.createProduct(product));
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:update')")
    public R<ProductVO> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return R.ok(productService.updateProduct(id, product));
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    public R<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return R.ok();
    }
}
