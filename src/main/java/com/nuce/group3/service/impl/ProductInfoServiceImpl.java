package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductInfoRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.ProductInfoResponse;
import com.nuce.group3.data.model.Category;
import com.nuce.group3.data.model.ProductDetail;
import com.nuce.group3.data.model.ProductInfo;
import com.nuce.group3.data.repo.CategoryRepo;
import com.nuce.group3.data.repo.ProductDetailRepo;
import com.nuce.group3.data.repo.ProductInfoRepo;
import com.nuce.group3.data.repo.VatDetailRepo;
import com.nuce.group3.enums.EnumStatus;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.ProductDetailService;
import com.nuce.group3.service.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProductInfoServiceImpl implements ProductInfoService {
    @Autowired
    private ProductInfoRepo productInfoRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private VatDetailRepo vatDetailRepo;

    @Autowired
    private ProductDetailRepo productDetailRepo;

    @Autowired
    private ProductDetailService productDetailService;

    @Override
    public void save(ProductInfoRequest productInfoRequest) throws LogicException, ResourceNotFoundException {
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByNameAndActiveFlag(productInfoRequest.getName(), 1);
        if (productInfoOptional.isPresent()) {
            throw new LogicException("Product Existed", HttpStatus.BAD_REQUEST);
        }
        Optional<Category> categoryOptional = categoryRepo.findById(productInfoRequest.getCategoryId());
        if (!categoryOptional.isPresent()) {
            throw new ResourceNotFoundException("Category with id " + productInfoRequest.getCategoryId() + " not found");
        }
        ProductInfo productInfo = new ProductInfo();
        productInfo.setCategory(categoryOptional.get());
        productInfo.setName(productInfoRequest.getName());
        productInfo.setDescription(productInfoRequest.getDescription());
        productInfo.setCreateDate(new Date());
        productInfo.setUpdateDate(new Date());
        productInfo.setActiveFlag(1);
        productInfo.setDescription(productInfoRequest.getDescription());
        productInfo.setImgName(productInfoRequest.getImgName());
        productInfo.setImgUrl(productInfoRequest.getImgUrl());
        productInfo.setQty(0);
        productInfo.setPriceIn(new BigDecimal(1));
        productInfo.setPriceOut(new BigDecimal(1));
        productInfoRepo.save(productInfo);
    }

    @Override
    public List<ProductInfoResponse> getAll() {
        List<ProductInfoResponse> productInfoResponses = new ArrayList<>();
        productInfoRepo.findProductInfoByActiveFlag(1).forEach(productInfo -> {
            ProductInfoResponse productInfoResponse = ProductInfoResponse.builder()
                    .name(productInfo.getName())
                    .description(productInfo.getDescription())
                    .imgName(productInfo.getImgName())
                    .createDate(productInfo.getCreateDate())
                    .updateDate(productInfo.getUpdateDate())
                    .categoryName(productInfo.getCategory().getName())
                    .qty(productInfo.getQty())
                    .imgUrl(productInfo.getImgUrl())
                    .priceIn(productInfo.getPriceIn().equals(new BigDecimal("1.00")) ? new BigDecimal(0) : productInfo.getPriceIn())
                    .priceOut(productInfo.getPriceOut().equals(new BigDecimal("1.00")) ? new BigDecimal(0) : productInfo.getPriceOut())
                    .categoryId(productInfo.getCategory().getId())
                    .build();
            productInfoResponses.add(productInfoResponse);
        });
        return productInfoResponses;
    }

    @Override
    public GenericResponse findProductInfoByFilter(String name, String categoryName, Integer qtyFrom, Integer qtyTo, BigDecimal priceFrom, BigDecimal priceTo, Integer page, Integer size) {
        List<ProductInfoResponse> productInfoResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        productInfoRepo.findProductInfoByFilter(name, categoryName, qtyFrom, qtyTo, priceFrom, priceTo, PageRequest.of(page, size)).forEach(productInfo -> {
            ProductInfoResponse productInfoResponse = ProductInfoResponse.builder()
                    .id(productInfo.getId())
                    .name(productInfo.getName())
                    .description(productInfo.getDescription())
                    .imgName(productInfo.getImgName())
                    .imgUrl(productInfo.getImgUrl())
                    .createDate(productInfo.getCreateDate())
                    .updateDate(productInfo.getUpdateDate())
                    .categoryName(productInfo.getCategory().getName())
                    .qty(productInfo.getQty())
                    .priceIn(productInfo.getPriceIn().equals(new BigDecimal("1.00")) ? new BigDecimal(0) : productInfo.getPriceIn())
                    .priceOut(productInfo.getPriceOut().equals(new BigDecimal("1.00")) ? new BigDecimal(0) : productInfo.getPriceOut())
                    .categoryId(productInfo.getCategory().getId())
                    .build();
            productInfoResponses.add(productInfoResponse);
        });
        return new GenericResponse(productInfoResponses.size(), productInfoResponses);
    }

    @Override
    public ProductInfoResponse findProductInfoById(Integer productId) throws ResourceNotFoundException {
        if (productId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(productId,1);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product info with " + productId + " not found!");
        }
        return ProductInfoResponse.builder()
                .id(productInfoOptional.get().getId())
                .name(productInfoOptional.get().getName())
                .description(productInfoOptional.get().getDescription())
                .qty(productInfoOptional.get().getQty())
                .categoryName(productInfoOptional.get().getCategory().getName())
                .imgName(productInfoOptional.get().getImgName())
                .imgUrl(productInfoOptional.get().getImgUrl())
                .createDate(productInfoOptional.get().getCreateDate())
                .updateDate(productInfoOptional.get().getUpdateDate())
                .priceIn(productInfoOptional.get().getPriceIn().equals(new BigDecimal("1.00")) ? new BigDecimal(0) : productInfoOptional.get().getPriceIn())
                .priceOut(productInfoOptional.get().getPriceOut().equals(new BigDecimal("1.00")) ? new BigDecimal(0) : productInfoOptional.get().getPriceOut())
                .categoryId(productInfoOptional.get().getCategory().getId())
                .build();
    }

    @Override
    public ProductInfoResponse edit(Integer productId, ProductInfoRequest productInfoRequest) throws ResourceNotFoundException, LogicException {
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(productId, 1);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product info with " + productId + " not found!");
        }
        Optional<ProductInfo> productInfoOptionalByName = productInfoRepo.findProductInfoByNameAndActiveFlag(productInfoRequest.getName(), 1);
        if (!productInfoRequest.getName().equals(productInfoOptional.get().getName()) && productInfoOptionalByName.isPresent()) {
            throw new ResourceNotFoundException("Product info with " + productInfoRequest.getName() + " is existed!");
        }
        ProductInfo productInfo = productInfoOptional.get();
        productInfo.setName(productInfoRequest.getName());
        Optional<Category> categoryOptional = categoryRepo.findCategoryByActiveFlagAndId(1, productInfoRequest.getCategoryId());
        if (!categoryOptional.isPresent()) {
            throw new ResourceNotFoundException("Category with " + productInfoRequest.getCategoryId() + " not found!");
        }
        productInfo.setCategory(categoryOptional.get());
        productInfo.setDescription(productInfoRequest.getDescription());
        if (productInfoRequest.getImgName() != null || !productInfoRequest.getImgName().isBlank()) {
            productInfo.setImgName(productInfoRequest.getImgName());
            productInfo.setImgUrl(productInfoRequest.getImgUrl());
        }
        productInfo.setUpdateDate(new Date());
        try {
            productInfoRepo.save(productInfo);
            return ProductInfoResponse.builder()
                    .id(productInfo.getId())
                    .categoryName(productInfo.getCategory().getName())
                    .imgName(productInfo.getImgName())
                    .imgUrl(productInfo.getImgUrl())
                    .updateDate(productInfo.getUpdateDate())
                    .description(productInfo.getDescription())
                    .priceIn(productInfo.getPriceIn().equals(new BigDecimal("1.00")) ? new BigDecimal(0) : productInfo.getPriceIn())
                    .priceOut(productInfo.getPriceOut().equals(new BigDecimal("1.00")) ? new BigDecimal(0) : productInfo.getPriceOut())
                    .qty(productInfo.getQty())
                    .categoryId(productInfo.getCategory().getId())
                    .build();
        } catch (Exception e) {
            throw new LogicException("Edit error", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Integer productId) throws ResourceNotFoundException, LogicException {
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(productId, 1);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product info with " + productId + " not found!");
        }
        productInfoOptional.get().setActiveFlag(0);

        List<ProductDetail> productDetailList = productDetailRepo.findProductDetailByProductInfoAndStatus(productId, EnumStatus.INVALID.toString());
        if (!productDetailList.isEmpty()) {
            throw new LogicException("The product has been purchased!", HttpStatus.BAD_REQUEST);
        } else {
            for (ProductDetail productDetail : productDetailList) {
                productDetailService.delete(productDetail.getId(), true);
            }
        }
        productInfoRepo.save(productInfoOptional.get());
    }
}
