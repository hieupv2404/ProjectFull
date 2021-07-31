package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductDetailRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.ProductDetailResponse;
import com.nuce.group3.data.model.*;
import com.nuce.group3.data.repo.*;
import com.nuce.group3.enums.EnumStatus;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.ProductDetailService;
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
public class ProductDetailServiceImpl implements ProductDetailService {
    @Autowired
    private ProductDetailRepo productDetailRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ProductInfoRepo productInfoRepo;

    @Autowired
    private IssueDetailRepo issueDetailRepo;

    @Autowired
    private ShelfRepo shelfRepo;

    @Autowired
    private ProductStatusListRepo productStatusListRepo;

    @Autowired
    private ProductStatusDetailRepo productStatusDetailRepo;

    @Override
    public List<ProductDetailResponse> getAll(Integer page, Integer size) {
        List<ProductDetailResponse> productDetailResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        productDetailRepo.findProductDetailByActiveFlag(1, PageRequest.of(page, size)).forEach(productDetail -> {
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
                    .productInfoId(productDetail.getProductInfo().getId())
                    .productStatusId(productDetail.getProductStatusList().getId())
                    .shelfId(productDetail.getShelf().getId())
                    .build();
            productDetailResponses.add(productDetailResponse);
        });
        return productDetailResponses;
    }

    @Override
    public GenericResponse findProductDetailByFilter(String name, String imei, Integer branchId, Integer page, Integer size) {
        List<ProductDetailResponse> productDetailResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        productDetailRepo.findProductDetailByFilter(name, imei, branchId, PageRequest.of(page, size)).forEach(productDetail -> {
            ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                    .id(productDetail.getId())
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
                    .productInfoId(productDetail.getProductInfo().getId())
                    .productStatusId(productDetail.getProductStatusList().getId())
                    .shelfId(productDetail.getShelf().getId())
                    .build();
            productDetailResponses.add(productDetailResponse);
        });
        return new GenericResponse(productDetailResponses.size(), productDetailResponses);
    }

    @Override
    public ProductDetailResponse findProductDetailById(Integer productId) throws ResourceNotFoundException {
        if (productId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByIdAndActiveFlag(productId, 1);
        if (!productDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Product detail with " + productId + " not found!");
        }
        ProductDetail productDetail = productDetailOptional.get();
        return ProductDetailResponse.builder()
                .id(productDetail.getId())
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
                .productInfoId(productDetail.getProductInfo().getId())
                .productStatusId(productDetail.getProductStatusList().getId())
                .shelfId(productDetail.getShelf().getId())
                .build();
    }

    @Override
    public void save(ProductDetailRequest productDetailRequest) throws LogicException, ResourceNotFoundException {
        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByImeiAndActiveFlag(productDetailRequest.getImei(), 1);
        if (productDetailOptional.isPresent()) {
            throw new LogicException("Product Detail Existed", HttpStatus.BAD_REQUEST);
        }
        Optional<Shelf> shelfOptional = shelfRepo.findShelfByIdAndActiveFlag(productDetailRequest.getShelfId(), 1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Shelf with id " + productDetailRequest.getShelfId() + " not found");
        }
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(productDetailRequest.getProductId(), 1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Info with id " + productDetailRequest.getShelfId() + " not found");
        }
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByIdAndActiveFlag(productDetailRequest.getProductStatusListId(), 1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Status List with id " + productDetailRequest.getProductStatusListId() + " not found");
        }
        Optional<ProductStatusDetail> productStatusDetailOptionalForProductExist = productStatusDetailRepo.findProductStatusDetailByProductStatusAndProduct(productStatusListOptional.get().getId(), productDetailRequest.getProductId());
        if (!productStatusDetailOptionalForProductExist.isPresent()) {
            throw new LogicException("Product Info doesn't exist in Product Status List", HttpStatus.BAD_REQUEST);
        }
        ProductDetail productDetail = new ProductDetail();
        productDetail.setShelf(shelfOptional.get());
        productDetail.setProductInfo(productInfoOptional.get());
        productDetail.setProductStatusList(productStatusListOptional.get());
        productDetail.setActiveFlag(1);
        productDetail.setCreateDate(new Date());
        productDetail.setUpdateDate(new Date());
        productDetail.setImei(productDetailRequest.getImei());
        productDetail.setStatus(EnumStatus.VALID);
        productDetailRepo.save(productDetail);

        shelfOptional.get().setQty(shelfOptional.get().getQty() + 1);
        shelfRepo.save(shelfOptional.get());
    }

    @Override
    public ProductDetailResponse edit(Integer productId, ProductDetailRequest productDetailRequest) throws ResourceNotFoundException, LogicException {
        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByIdAndActiveFlag(productId, 1);
        if (!productDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Product detail with " + productId + " not found!");
        }
        ProductDetail productDetail = productDetailOptional.get();
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByIdAndActiveFlag(productDetailRequest.getProductStatusListId(), 1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Status List with id " + productDetailRequest.getProductStatusListId() + " not found");
        }
        Optional<Shelf> shelfOptional = shelfRepo.findShelfByIdAndActiveFlag(productDetailRequest.getShelfId(), 1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Shelf with id " + productDetailRequest.getShelfId() + " not found");
        }
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(productDetailRequest.getProductId(), 1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Info with id " + productDetailRequest.getShelfId() + " not found");
        }
        Optional<ProductDetail> productDetailByImei = productDetailRepo.findProductDetailByImeiAndActiveFlag(productDetailRequest.getImei(), 1);
        if (!productDetailRequest.getImei().equals(productDetailOptional.get().getImei()) && productDetailByImei.isPresent()) {
            throw new ResourceNotFoundException("Product detail with imei" + productDetailRequest.getImei() + " existed!");
        }

        productDetail.getShelf().setQty(productDetail.getShelf().getQty() - 1);
        shelfRepo.save(productDetail.getShelf());

        productDetail.setProductInfo(productInfoOptional.get());
        productDetail.setShelf(shelfOptional.get());
        productDetail.setProductStatusList(productStatusListOptional.get());
        productDetail.setImei(productDetailRequest.getImei());
        productDetail.setUpdateDate(new Date());
        try {
            productDetailRepo.save(productDetail);

            shelfOptional.get().setQty(shelfOptional.get().getQty() + 1);
            shelfRepo.save(shelfOptional.get());
            return ProductDetailResponse.builder()
                    .id(productDetail.getId())
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
                    .productInfoId(productDetail.getProductInfo().getId())
                    .productStatusId(productDetail.getProductStatusList().getId())
                    .shelfId(productDetail.getShelf().getId())
                    .build();
        } catch (Exception e) {
            throw new LogicException("Edit error", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Integer productId, boolean isDeletedParent) throws ResourceNotFoundException, LogicException {
        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByIdAndActiveFlag(productId, 1);
        if (!productDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Product detail with " + productId + " not found!");
        }
        if (productDetailOptional.get().getStatus().equals(EnumStatus.INVALID)) {
            throw new LogicException("The product has been purchased", HttpStatus.BAD_REQUEST);
        }

        productDetailOptional.get().setActiveFlag(0);
        productDetailRepo.save(productDetailOptional.get());

        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(productDetailOptional.get().getProductInfo().getId(), 1);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product info with ID: " + productDetailOptional.get().getProductInfo().getId() + " not found");
        }

        if (!isDeletedParent) {
            productInfoOptional.get().setQty(productInfoOptional.get().getQty() - 1);
            if (productInfoOptional.get().getQty() == 0) {
                productInfoOptional.get().setPriceIn(new BigDecimal("1"));
                productInfoOptional.get().setPriceOut(new BigDecimal("1"));
            }
            productInfoRepo.save(productInfoOptional.get());
        }


        Optional<Shelf> shelfOptional = shelfRepo.findShelfByIdAndActiveFlag(productDetailOptional.get().getShelf().getId(), 1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Shelf with ID: " + productDetailOptional.get().getShelf().getId() + " not found");
        }

        shelfOptional.get().setQty(shelfOptional.get().getQty() - 1);
        shelfRepo.save(shelfOptional.get());
    }

    @Override
    public void changeStatusToValid(Integer productDetailId, String enumStatus) throws ResourceNotFoundException, LogicException {
        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByIdAndActiveFlag(productDetailId, 1);
        if (!productDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Product detail with " + productDetailId + " not found!");
        }
        if (productDetailOptional.get().getStatus() == EnumStatus.VALID) {
            throw new LogicException("Status Product detail is not available", HttpStatus.BAD_REQUEST);
        }
        productDetailOptional.get().setStatus(EnumStatus.VALID);
        productDetailRepo.save(productDetailOptional.get());

        productDetailOptional.get().getShelf().setQty(productDetailOptional.get().getShelf().getQty() + 1);
        shelfRepo.save(productDetailOptional.get().getShelf());

    }

    @Override
    public void changeStatusToInValid(Integer productDetailId, String enumStatus) throws ResourceNotFoundException, LogicException {
        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByIdAndActiveFlag(productDetailId, 1);
        if (!productDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Product detail with " + productDetailId + " not found!");
        }
        if (productDetailOptional.get().getStatus() == EnumStatus.INVALID) {
            throw new LogicException("Status Product detail is not available", HttpStatus.BAD_REQUEST);
        }
        productDetailOptional.get().setStatus(EnumStatus.INVALID);
        productDetailRepo.save(productDetailOptional.get());

        productDetailOptional.get().getShelf().setQty(productDetailOptional.get().getShelf().getQty() - 1);
        shelfRepo.save(productDetailOptional.get().getShelf());
    }
}
