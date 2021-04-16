package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.CategoryRequest;
import com.nuce.group3.controller.dto.request.ProductInfoRequest;
import com.nuce.group3.controller.dto.response.ProductInfoResponse;
import com.nuce.group3.data.model.Category;
import com.nuce.group3.data.model.ProductInfo;
import com.nuce.group3.data.repo.CategoryRepo;
import com.nuce.group3.data.repo.ProductInfoRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductInfoServiceImpl implements ProductInfoService {
    @Autowired
    private ProductInfoRepo productInfoRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Override
    public void save(ProductInfoRequest productInfoRequest) throws LogicException, ResourceNotFoundException {
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByNameAndActiveFlag(productInfoRequest.getName(), 1);
        if (productInfoOptional.isPresent()) {
            throw new LogicException("Product Existed", HttpStatus.BAD_REQUEST);
        }
        Optional<Category> categoryOptional = categoryRepo.findById(productInfoRequest.getCategoryId());
        if (!categoryOptional.isPresent()) {
            throw new ResourceNotFoundException("Categroy with id " + productInfoRequest.getCategoryId() + " not found");
        }
        ProductInfo productInfo = new ProductInfo();
        productInfo.setCategory(categoryOptional.get());
        productInfo.setName(productInfoRequest.getName());
        productInfo.setDescription(productInfoRequest.getDescription());
        productInfo.setCreateDate(new Date());
        productInfo.setUpdateDate(new Date());
        productInfo.setActiveFlag(1);
        productInfo.setDescription(productInfoRequest.getDescription());
        productInfo.setImgUrl(productInfoRequest.getImgUrl());
        productInfo.setQty(0);
        productInfoRepo.save(productInfo);
    }

    @Override
    public List<ProductInfoResponse> getAll() {
        List<ProductInfoResponse> productInfoResponses = new ArrayList<>();
        productInfoRepo.findProductInfoByActiveFlag(1).forEach(productInfo -> {
            ProductInfoResponse productInfoResponse = ProductInfoResponse.builder()
                    .name(productInfo.getName())
                    .description(productInfo.getDescription())
                    .imgUrl(productInfo.getImgUrl())
                    .createDate(productInfo.getCreateDate())
                    .updateDate(productInfo.getUpdateDate())
                    .categoryName(productInfo.getCategory().getName())
                    .qty(productInfo.getQty())
                    .priceIn(productInfo.getPriceIn())
                    .priceOut(productInfo.getPriceOut())
                    .build();
            productInfoResponses.add(productInfoResponse);
        });
        return productInfoResponses;
    }

    @Override
    public List<ProductInfoResponse> findProductInfoByFilter(String name, String categoryName, int qtyFrom, int qtyTo, BigDecimal priceFrom, BigDecimal priceTo) {
        List<ProductInfoResponse> productInfoResponses = new ArrayList<>();
        productInfoRepo.findProductInfoByFilter(name, categoryName, qtyFrom, qtyTo, priceFrom, priceTo).forEach(productInfo -> {
            ProductInfoResponse productInfoResponse = ProductInfoResponse.builder()
                    .name(productInfo.getName())
                    .description(productInfo.getDescription())
                    .imgUrl(productInfo.getImgUrl())
                    .createDate(productInfo.getCreateDate())
                    .updateDate(productInfo.getUpdateDate())
                    .categoryName(productInfo.getCategory().getName())
                    .qty(productInfo.getQty())
                    .priceIn(productInfo.getPriceIn())
                    .priceOut(productInfo.getPriceOut())
                    .build();
            productInfoResponses.add(productInfoResponse);
        });
        return productInfoResponses;
    }

    @Override
    public ProductInfoResponse findProductInfoById(Integer productId) throws ResourceNotFoundException {
        if (productId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByActiveFlagAndId(1, productId);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product info with " + productId + " not found!");
        }
        return ProductInfoResponse.builder()
                .name(productInfoOptional.get().getName())
                .description(productInfoOptional.get().getDescription())
                .qty(productInfoOptional.get().getQty())
                .categoryName(productInfoOptional.get().getCategory().getName())
                .imgUrl(productInfoOptional.get().getImgUrl())
                .createDate(productInfoOptional.get().getCreateDate())
                .updateDate(productInfoOptional.get().getUpdateDate())
                .priceIn(productInfoOptional.get().getPriceIn())
                .priceOut(productInfoOptional.get().getPriceOut())
                .build();
    }

    @Override
    public ProductInfoResponse edit(Integer productId, ProductInfoRequest productInfoRequest) throws ResourceNotFoundException, LogicException {
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByActiveFlagAndId(1, productId);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product info with " + productId + " not found!");
        }
        ProductInfo productInfo = productInfoOptional.get();
        productInfo.setName(productInfoRequest.getName());
        Optional<Category> categoryOptional = categoryRepo.findCategoryByActiveFlagAndId(1, productInfoRequest.getCategoryId());
        if (!categoryOptional.isPresent()) {
            throw new ResourceNotFoundException("Category with " + productInfoRequest.getCategoryId() + " not found!");
        }
        productInfo.setCategory(categoryOptional.get());
        productInfo.setDescription(productInfoRequest.getDescription());
        productInfo.setImgUrl(productInfoRequest.getImgUrl());
        productInfo.setUpdateDate(new Date());
        try {
            productInfoRepo.save(productInfo);
            return ProductInfoResponse.builder()
                    .categoryName(productInfo.getCategory().getName())
                    .imgUrl(productInfo.getImgUrl())
                    .updateDate(productInfo.getUpdateDate())
                    .description(productInfo.getDescription())
                    .qty(productInfo.getQty())
                    .build();
        } catch (Exception e) {
            throw new LogicException("Edit error", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Integer productId) throws ResourceNotFoundException {
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByActiveFlagAndId(1, productId);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product info with " + productId + " not found!");
        }
        productInfoOptional.get().setActiveFlag(0);
        productInfoRepo.save(productInfoOptional.get());
    }
}
