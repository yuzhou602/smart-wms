package com.smartwms.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.common.response.PageResult;
import com.smartwms.product.entity.Product;
import com.smartwms.product.vo.ProductVO;

public interface ProductService extends IService<Product> {

    PageResult<ProductVO> listProducts(int page, int pageSize, String keyword, Long categoryId, Integer status);

    ProductVO getProductById(Long id);

    ProductVO createProduct(Product product);

    ProductVO updateProduct(Long id, Product product);

    void deleteProduct(Long id);
}
