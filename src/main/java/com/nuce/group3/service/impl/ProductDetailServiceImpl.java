package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductDetailRequest;
import com.nuce.group3.controller.dto.response.ProductDetailResponse;
import com.nuce.group3.data.model.*;
import com.nuce.group3.data.repo.*;
import com.nuce.group3.data.repo.ProductDetailRepo;
import com.nuce.group3.enums.EnumStatus;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductDetailServiceImpl implements ProductDetailService {
    @Autowired
    private ProductDetailRepo productDetailRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ProductInfoRepo productInfoRepo;

    @Autowired
    private SupplierRepo supplierRepo;

    @Autowired
    private ShelfRepo shelfRepo;

    @Override
    public List<ProductDetailResponse> getAll(Integer page, Integer size) {
        List<ProductDetailResponse> productDetailResponses = new ArrayList<>();
        if (page==null) page = 0;
        if (size==null) size = 5;
        productDetailRepo.findProductDetailByActiveFlag(1, PageRequest.of(page,size)).forEach(productDetail -> {
            ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                    .productName(productDetail.getProductInfo().getName())
                    .supplierName(productDetail.getProductStatusList().getVat().getSupplier().getName())
                    .categoryName(productDetail.getProductInfo().getCategory().getName())
                    .createDate(productDetail.getCreateDate())
                    .updateDate(productDetail.getUpdateDate())
                    .priceIn(productDetail.getProductInfo().getPriceIn())
                    .priceOut(productDetail.getProductInfo().getPriceOut())
                    .imei(productDetail.getImei())
                    .shelfName(productDetail.getShelf().getName())
                    .status(productDetail.getStatus())
                    .build();
            productDetailResponses.add(productDetailResponse);
        });
        return productDetailResponses;
    }

    @Override
    public List<ProductDetailResponse> findProductDetailByFilter(String name, String supplierName, String imei, Integer page, Integer size) {
        List<ProductDetailResponse> productDetailResponses = new ArrayList<>();
        if (page==null) page = 0;
        if (size==null) size = 5;
        productDetailRepo.findProductDetailByFilter(name, supplierName, imei, PageRequest.of(page, size)).forEach(productDetail -> {
            ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                    .productName(productDetail.getProductInfo().getName())
                    .supplierName(productDetail.getProductStatusList().getVat().getSupplier().getName())
                    .categoryName(productDetail.getProductInfo().getCategory().getName())
                    .createDate(productDetail.getCreateDate())
                    .updateDate(productDetail.getUpdateDate())
                    .priceIn(productDetail.getProductInfo().getPriceIn())
                    .priceOut(productDetail.getProductInfo().getPriceOut())
                    .imei(productDetail.getImei())
                    .shelfName(productDetail.getShelf().getName())
                    .status(productDetail.getStatus())
                    .build();
            productDetailResponses.add(productDetailResponse);
        });
        return productDetailResponses;
    }

    @Override
    public ProductDetailResponse findProductDetailById(Integer productId) throws ResourceNotFoundException {
        if (productId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByIdAndActiveFlag(productId,1);
        if (!productDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Product detail with " + productId + " not found!");
        }
        ProductDetail productDetail = productDetailOptional.get();
        return ProductDetailResponse.builder()
                .productName(productDetail.getProductInfo().getName())
                .supplierName(productDetail.getProductStatusList().getVat().getSupplier().getName())
                .categoryName(productDetail.getProductInfo().getCategory().getName())
                .createDate(productDetail.getCreateDate())
                .updateDate(productDetail.getUpdateDate())
                .priceIn(productDetail.getProductInfo().getPriceIn())
                .priceOut(productDetail.getProductInfo().getPriceOut())
                .imei(productDetail.getImei())
                .shelfName(productDetail.getShelf().getName())
                .status(productDetail.getStatus())
                .build();
    }

    @Override
    public void save(ProductDetailRequest productDetailRequest) throws LogicException, ResourceNotFoundException {
        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByImeiAndActiveFlag(productDetailRequest.getImei(), 1);
        if (productDetailOptional.isPresent()) {
            throw new LogicException("Product Detail Existed", HttpStatus.BAD_REQUEST);
        }
        Optional<Supplier> supplierOptional = supplierRepo.findSupplierByIdAndActiveFlag(productDetailRequest.getSupplierId(), 1);
        if (!supplierOptional.isPresent()) {
            throw new ResourceNotFoundException("Supplier with id " + productDetailRequest.getSupplierId() + " not found");
        }
        Optional<Shelf> shelfOptional = shelfRepo.findShelfByIdAndActiveFlag(productDetailRequest.getShelfId(),1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Shelf with id " + productDetailRequest.getShelfId() + " not found");
        }
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(productDetailRequest.getProductId(),1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Info with id " + productDetailRequest.getShelfId() + " not found");
        }

        ProductDetail productDetail = new ProductDetail();
        productDetail.setShelf(shelfOptional.get());
        productDetail.setProductInfo(productInfoOptional.get());
        productDetail.setActiveFlag(1);
        productDetail.setCreateDate(new Date());
        productDetail.setUpdateDate(new Date());
        productDetail.setImei(productDetailRequest.getImei());
        productDetail.setStatus(EnumStatus.VALID);
        productDetailRepo.save(productDetail);
    }

    @Override
    public ProductDetailResponse edit(Integer productId, ProductDetailRequest productDetailRequest) throws ResourceNotFoundException, LogicException {
        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByIdAndActiveFlag(productId, 1);
        if (!productDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Product detail with " + productId + " not found!");
        }
        ProductDetail productDetail = productDetailOptional.get();
        Optional<Supplier> supplierOptional = supplierRepo.findSupplierByIdAndActiveFlag(productDetailRequest.getSupplierId(), 1);
        if (!supplierOptional.isPresent()) {
            throw new ResourceNotFoundException("Supplier with id " + productDetailRequest.getSupplierId() + " not found");
        }
        Optional<Shelf> shelfOptional = shelfRepo.findShelfByIdAndActiveFlag(productDetailRequest.getShelfId(),1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Shelf with id " + productDetailRequest.getShelfId() + " not found");
        }
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(productDetailRequest.getProductId(),1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Info with id " + productDetailRequest.getShelfId() + " not found");
        }
        Optional<ProductDetail> productDetailByImei = productDetailRepo.findProductDetailByImeiAndActiveFlag(productDetailRequest.getImei(), 1);
        if (productDetailByImei.isPresent()) {
            throw new ResourceNotFoundException("Product detail with imei" + productDetailRequest.getImei() + " existed!");
        }

        productDetail.setProductInfo(productInfoOptional.get());
//        productDetail.setSupplier(supplierOptional.get());
        productDetail.setShelf(shelfOptional.get());
        productDetail.setImei(productDetailRequest.getImei());
        productDetail.setUpdateDate(new Date());
        try {
            productDetailRepo.save(productDetail);
            return ProductDetailResponse.builder()
                    .productName(productDetail.getProductInfo().getName())
                    .supplierName(productDetail.getProductStatusList().getVat().getSupplier().getName())
                    .categoryName(productDetail.getProductInfo().getCategory().getName())
                    .createDate(productDetail.getCreateDate())
                    .updateDate(productDetail.getUpdateDate())
                    .priceIn(productDetail.getProductInfo().getPriceIn())
                    .priceOut(productDetail.getProductInfo().getPriceOut())
                    .imei(productDetail.getImei())
                    .shelfName(productDetail.getShelf().getName())
                    .status(productDetail.getStatus())
                    .build();
        } catch (Exception e) {
            throw new LogicException("Edit error", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Integer productId) throws ResourceNotFoundException {
        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByIdAndActiveFlag(productId,1);
        if (!productDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Product detail with " + productId + " not found!");
        }
        productDetailOptional.get().setActiveFlag(0);
        productDetailRepo.save(productDetailOptional.get());
    }
}
