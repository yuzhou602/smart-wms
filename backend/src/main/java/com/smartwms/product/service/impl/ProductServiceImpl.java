package com.smartwms.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.ResultCode;
import com.smartwms.product.entity.Product;
import com.smartwms.product.mapper.ProductMapper;
import com.smartwms.product.service.ProductService;
import com.smartwms.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public PageResult<ProductVO> listProducts(int page, int pageSize, String keyword, Long categoryId, Integer status) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getProductCode, keyword)
                    .or().like(Product::getProductName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByDesc(Product::getCreatedAt);

        Page<Product> pageResult = productMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<ProductVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public ProductVO getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToVO(product);
    }

    @Override
    @Transactional
    public ProductVO createProduct(Product product) {
        productMapper.insert(product);
        return convertToVO(product);
    }

    @Override
    @Transactional
    public ProductVO updateProduct(Long id, Product product) {
        Product existing = productMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        product.setId(id);
        productMapper.updateById(product);
        return convertToVO(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        productMapper.deleteById(id);
    }

    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setProductCode(product.getProductCode());
        vo.setProductName(product.getProductName());
        vo.setCategoryId(product.getCategoryId());
        vo.setCategoryName(productMapper.getCategoryNameById(product.getCategoryId()));
        vo.setBrand(product.getBrand());
        vo.setDescription(product.getDescription());
        vo.setImage(product.getImage());
        vo.setStatus(product.getStatus());
        vo.setSkuCount(productMapper.countSkusByProductId(product.getId()));
        vo.setCreatedAt(product.getCreatedAt());
        return vo;
    }
}
