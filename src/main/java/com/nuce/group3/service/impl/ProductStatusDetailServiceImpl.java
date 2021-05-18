package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductStatusDetailRequest;
import com.nuce.group3.controller.dto.response.ProductStatusDetailResponse;
import com.nuce.group3.data.model.ProductInfo;
import com.nuce.group3.data.model.ProductStatusDetail;
import com.nuce.group3.data.model.ProductStatusList;
import com.nuce.group3.data.repo.*;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.ProductStatusDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductStatusDetailServiceImpl implements ProductStatusDetailService {

    @Autowired
    private ProductStatusListRepo productStatusListRepo;

    @Autowired
    private VatDetailRepo vatDetailRepo;

    @Autowired
    private ProductStatusDetailRepo productStatusDetailRepo;

    @Autowired
    private ProductInfoRepo productInfoRepo;

    @Autowired
    private VatRepo vatRepo;

    @Override
    public List<ProductStatusDetailResponse> getAll(int type, Integer page, Integer size) {
        List<ProductStatusDetailResponse> productStatusDetailResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        productStatusDetailRepo.findProductStatusDetailByActiveFlag(type, PageRequest.of(page, size)).forEach(productStatusDetail -> {
            ProductStatusDetailResponse productStatusDetailResponse = ProductStatusDetailResponse.builder()
                    .priceTotal(productStatusDetail.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetail.getQty())))
                    .priceOne(productStatusDetail.getPriceOne())
                    .qty(productStatusDetail.getQty())
                    .qtyRest(productStatusDetail.getQtyRest())
                    .productInfo(productStatusDetail.getProductInfo().getName())
                    .productStatusListCode(productStatusDetail.getProductStatusList().getCode())
                    .build();
            productStatusDetailResponses.add(productStatusDetailResponse);
        });
        return productStatusDetailResponses;
    }

    @Override
    public List<ProductStatusDetailResponse> findProductStatusDetailByFilter(BigDecimal priceTotalFrom, BigDecimal priceTotalTo, String productStatusListCode, String productInfo, int type, Integer page, Integer size) {
        List<ProductStatusDetailResponse> productStatusDetailResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        productStatusDetailRepo.findProductStatusDetailByFilter(priceTotalFrom, priceTotalTo, productStatusListCode, productInfo, type, PageRequest.of(page, size)).forEach(productStatusDetail -> {
            ProductStatusDetailResponse productStatusDetailResponse = ProductStatusDetailResponse.builder()
                    .priceTotal(productStatusDetail.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetail.getQty())))
                    .priceOne(productStatusDetail.getPriceOne())
                    .qty(productStatusDetail.getQty())
                    .qtyRest(productStatusDetail.getQtyRest())
                    .productInfo(productStatusDetail.getProductInfo().getName())
                    .productStatusListCode(productStatusDetail.getProductStatusList().getCode())
                    .build();
            productStatusDetailResponses.add(productStatusDetailResponse);
        });
        return productStatusDetailResponses;
    }

    @Override
    public ProductStatusDetailResponse findProductStatusDetailById(Integer productStatusDetailId) throws ResourceNotFoundException {
        if (productStatusDetailId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<ProductStatusDetail> productStatusDetailOptional = productStatusDetailRepo.findProductStatusDetailByIdAndActiveFlag(productStatusDetailId, 1);
        if (!productStatusDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatus Detail with " + productStatusDetailId + " not found!");
        }
        ProductStatusDetail productStatusDetail = productStatusDetailOptional.get();
        return ProductStatusDetailResponse.builder()
                .priceTotal(productStatusDetail.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetail.getQty())))
                .priceOne(productStatusDetail.getPriceOne())
                .qty(productStatusDetail.getQty())
                .qtyRest(productStatusDetail.getQtyRest())
                .productInfo(productStatusDetail.getProductInfo().getName())
                .productStatusListCode(productStatusDetail.getProductStatusList().getCode())
                .build();
    }

    @Override
    public void save(ProductStatusDetailRequest productStatusDetailRequest) throws LogicException, ResourceNotFoundException {
        Optional<ProductStatusDetail> productStatusDetailOptional = productStatusDetailRepo.findProductStatusDetailByProductStatusAndProduct(productStatusDetailRequest.getProductStatusListId(), productStatusDetailRequest.getProductId());
        if (productStatusDetailOptional.isPresent()) {
            throw new LogicException("ProductStatus Detail Existed", HttpStatus.BAD_REQUEST);
        }
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByIdAndActiveFlag(productStatusDetailRequest.getProductStatusListId(), 1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Status List with id " + productStatusDetailRequest.getProductStatusListId() + " not found");
        }

        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(productStatusDetailRequest.getProductId(), 1);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Info with id: " + productStatusDetailRequest.getProductId() + " not found");
        }

        ProductStatusDetail productStatusDetail = new ProductStatusDetail();
        productStatusDetail.setProductStatusList(productStatusListOptional.get());
        productStatusDetail.setProductInfo(productInfoOptional.get());
        productStatusDetail.setQty(productStatusDetailRequest.getQty());
        productStatusDetail.setPriceOne(productStatusDetailRequest.getPriceOne());
        productStatusDetail.setActiveFlag(1);
        productStatusDetailRepo.save(productStatusDetail);
    }

    @Override
    public ProductStatusDetailResponse edit(Integer productStatusDetailId, ProductStatusDetailRequest productStatusDetailRequest) throws ResourceNotFoundException, LogicException {
        Optional<ProductStatusDetail> productStatusDetailOptional = productStatusDetailRepo.findProductStatusDetailByIdAndActiveFlag(productStatusDetailId, 1);
        if (!productStatusDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusDetail with ID: " + productStatusDetailId + " not found!");
        }
        ProductStatusDetail productStatusDetail = productStatusDetailOptional.get();
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByIdAndActiveFlag(productStatusDetailRequest.getProductStatusListId(), 1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatus with id " + productStatusDetailRequest.getProductStatusListId() + " not found");
        }

        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(productStatusDetailRequest.getProductId(), 1);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Info with id: " + productStatusDetailRequest.getProductId() + " not found");
        }

        Optional<ProductStatusDetail> productStatusDetailOptional2 = productStatusDetailRepo.findProductStatusDetailByProductStatusAndProduct(productStatusDetailRequest.getProductStatusListId(), productStatusDetailRequest.getProductId());
        if (productStatusDetailOptional2.isPresent()) {
            throw new LogicException("ProductStatus Detail Existed", HttpStatus.BAD_REQUEST);
        }

        productStatusDetail.setProductStatusList(productStatusListOptional.get());
        productStatusDetail.setProductInfo(productInfoOptional.get());
        productStatusDetail.setQty(productStatusDetailRequest.getQty());
        productStatusDetail.setPriceOne(productStatusDetailRequest.getPriceOne());
        try {
            productStatusDetailRepo.save(productStatusDetail);
            return ProductStatusDetailResponse.builder()
                    .priceTotal(productStatusDetail.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetail.getQty())))
                    .priceOne(productStatusDetail.getPriceOne())
                    .qty(productStatusDetail.getQty())
                    .qtyRest(productStatusDetail.getQtyRest())
                    .productInfo(productStatusDetail.getProductInfo().getName())
                    .productStatusListCode(productStatusDetail.getProductStatusList().getCode())
                    .build();
        } catch (Exception e) {
            throw new LogicException("Edit error", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Integer productStatusDetailId) throws ResourceNotFoundException {
        Optional<ProductStatusDetail> productStatusDetailOptional = productStatusDetailRepo.findProductStatusDetailByIdAndActiveFlag(productStatusDetailId, 1);
        if (!productStatusDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusDetail with " + productStatusDetailId + " not found!");
        }
        productStatusDetailOptional.get().setActiveFlag(0);
        productStatusDetailRepo.save(productStatusDetailOptional.get());
    }
}
