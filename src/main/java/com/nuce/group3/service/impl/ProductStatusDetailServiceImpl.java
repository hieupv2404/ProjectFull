package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductStatusDetailRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.ProductStatusDetailResponse;
import com.nuce.group3.data.model.ProductInfo;
import com.nuce.group3.data.model.ProductStatusDetail;
import com.nuce.group3.data.model.ProductStatusList;
import com.nuce.group3.data.model.VatDetail;
import com.nuce.group3.data.repo.*;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.ProductDetailService;
import com.nuce.group3.service.ProductStatusDetailService;
import com.nuce.group3.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
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
    private ProductDetailService productDetailService;

    @Autowired
    private VatRepo vatRepo;

    @Autowired
    private ProductDetailRepo productDetailRepo;

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

    public List<ProductStatusDetailResponse> findProductStatusDetailToExport(BigDecimal priceTotalFrom, BigDecimal priceTotalTo, String productStatusListCode, String productInfo, Integer type, Integer branchId) {
        List<ProductStatusDetailResponse> productStatusDetailResponses = new ArrayList<>();
        productStatusDetailRepo.findProductStatusDetailForExport(priceTotalFrom, priceTotalTo, productStatusListCode, productInfo, type, branchId).forEach(productStatusDetail -> {
            ProductStatusDetailResponse productStatusDetailResponse = ProductStatusDetailResponse.builder()
                    .id(productStatusDetail.getId())
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
    public GenericResponse findProductStatusDetailByFilter(BigDecimal priceTotalFrom, BigDecimal priceTotalTo, String productStatusListCode, String productInfo, int type, Integer branchId, Integer page, Integer size) {
        List<ProductStatusDetailResponse> productStatusDetailResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        productStatusDetailRepo.findProductStatusDetailByFilter(priceTotalFrom, priceTotalTo, productStatusListCode, productInfo, type, branchId, PageRequest.of(page, size)).forEach(productStatusDetail -> {
            ProductStatusDetailResponse productStatusDetailResponse = ProductStatusDetailResponse.builder()
                    .id(productStatusDetail.getId())
                    .priceTotal(productStatusDetail.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetail.getQty())))
                    .priceOne(productStatusDetail.getPriceOne())
                    .qty(productStatusDetail.getQty())
                    .qtyRest(productStatusDetail.getQtyRest())
                    .productInfo(productStatusDetail.getProductInfo().getName())
                    .productStatusListCode(productStatusDetail.getProductStatusList().getCode())
                    .build();
            productStatusDetailResponses.add(productStatusDetailResponse);
        });
        return new GenericResponse(productStatusDetailRepo.findProductStatusDetailByFilter(priceTotalFrom, priceTotalTo, productStatusListCode, productInfo, type, branchId, PageRequest.of(0, 1000)).size(), productStatusDetailResponses);
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
                .id(productStatusDetail.getId())
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

        Optional<VatDetail> vatDetailOptional = vatDetailRepo.findVatDetailByVatAndProduct(productStatusListOptional.get().getVat().getId(), productStatusDetailRequest.getProductId());
        if (!vatDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("The product is not existed in this Vat");
        }

        BigDecimal priceOneFromVatDetail = vatDetailOptional.get().getPriceOne();

        ProductStatusDetail productStatusDetailDone = new ProductStatusDetail();
        productStatusDetailDone.setProductStatusList(productStatusListOptional.get());
        productStatusDetailDone.setProductInfo(productInfoOptional.get());
        productStatusDetailDone.setQty(productStatusDetailRequest.getQty());
        if ((vatDetailOptional.get().getQty() - productStatusDetailRequest.getQty()) < 0) {
            throw new LogicException("Qty for import are unvaliable!", HttpStatus.BAD_REQUEST);
        }
        ;
        productStatusDetailDone.setQtyRest(vatDetailOptional.get().getQty() - productStatusDetailRequest.getQty());
        productStatusDetailDone.setPriceOne(priceOneFromVatDetail);
        productStatusDetailDone.setActiveFlag(1);
        productStatusDetailRepo.save(productStatusDetailDone);

        String codeForBackList = productStatusListOptional.get().getCode().replace("DONE", "BACK");
        Optional<ProductStatusList> productStatusListBackOptional = productStatusListRepo.findProductStatusListByCodeAndActiveFlag(codeForBackList, 1);
        if (!productStatusListBackOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Status List with Code " + codeForBackList + " not found");
        }

        ProductStatusDetail productStatusDetailBack = new ProductStatusDetail();
        productStatusDetailBack.setProductStatusList(productStatusListBackOptional.get());
        productStatusDetailBack.setProductInfo(productInfoOptional.get());


        productStatusDetailBack.setQty(productStatusDetailDone.getQtyRest());
        productStatusDetailBack.setQtyRest(productStatusDetailDone.getQty());
        productStatusDetailBack.setPriceOne(priceOneFromVatDetail);
        productStatusDetailBack.setActiveFlag(1);
        productStatusDetailRepo.save(productStatusDetailBack);

        BigDecimal currentPrice = productInfoOptional.get().getPriceIn().multiply(BigDecimal.valueOf(productInfoOptional.get().getQty()));
        BigDecimal newPrice = productStatusDetailDone.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetailDone.getQty()));
        int totalQty = productInfoOptional.get().getQty() + productStatusDetailDone.getQty();
        productInfoOptional.get().setPriceIn((currentPrice.add(newPrice)).divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_UP));
        productInfoOptional.get().setPriceOut(productInfoOptional.get().getPriceIn().add(productInfoOptional.get().getPriceIn().multiply(new BigDecimal(0.2))));
        productInfoOptional.get().setQty(totalQty);
        productInfoRepo.save(productInfoOptional.get());

        productStatusListOptional.get().setPrice(productStatusListOptional.get().getPrice().add(productStatusDetailDone.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetailDone.getQty()))));
        productStatusListRepo.save(productStatusListOptional.get());

        productStatusListBackOptional.get().setPrice(productStatusListBackOptional.get().getPrice().add(productStatusDetailBack.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetailBack.getQty()))));
        productStatusListRepo.save(productStatusListBackOptional.get());
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
        if (productStatusDetailOptional2.isPresent() && !productStatusDetailOptional2.equals(productStatusDetailOptional)) {
            throw new LogicException("ProductStatus Detail Existed", HttpStatus.BAD_REQUEST);
        }

        Optional<VatDetail> vatDetailOptional = vatDetailRepo.findVatDetailByVatAndProduct(productStatusListOptional.get().getVat().getId(), productStatusDetailRequest.getProductId());
        if (!vatDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("The product is not existed in this Vat");
        }


        BigDecimal priceOneFromVatDetail = vatDetailOptional.get().getPriceOne();

        BigDecimal oldPrice = productStatusDetail.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetail.getQty()));

        ProductInfo productInfo = productInfoOptional.get();
        int oldQty = productInfo.getQty();
        productInfo.setQty(productInfo.getQty() - productStatusDetail.getQty() + productStatusDetailRequest.getQty());
        productInfo.setPriceIn(productInfo.getPriceIn().multiply(BigDecimal.valueOf(oldQty)).subtract(productStatusDetail.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetail.getQty()))).divide(BigDecimal.valueOf(productInfo.getQty()), 2, RoundingMode.HALF_UP));


        productStatusDetail.setProductStatusList(productStatusListOptional.get());
        productStatusDetail.setProductInfo(productInfoOptional.get());
        productStatusDetail.setQty(productStatusDetailRequest.getQty());
        productStatusDetail.setPriceOne(priceOneFromVatDetail);

        BigDecimal currentPrice = productInfo.getPriceIn().multiply(BigDecimal.valueOf(productInfo.getQty()));
        BigDecimal newPrice = productStatusDetail.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetail.getQty()));


        try {
            productStatusDetailRepo.save(productStatusDetail);
            productStatusListOptional.get().setPrice(productStatusListOptional.get().getPrice().subtract(oldPrice).add(productStatusDetail.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetail.getQty()))));
            productStatusListRepo.save(productStatusListOptional.get());

            productInfo.setPriceIn((currentPrice.add(newPrice)).divide(BigDecimal.valueOf(productInfo.getQty() + productStatusDetail.getQty()), 2, RoundingMode.HALF_UP));
            productInfo.setPriceOut(productInfo.getPriceOut().add(productInfo.getPriceOut().multiply(new BigDecimal(0.2))));
            productInfoRepo.save(productInfo);

            return ProductStatusDetailResponse.builder()
                    .id(productStatusDetail.getId())
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
    public void delete(Integer productStatusDetailId, boolean isDeletedList) throws ResourceNotFoundException {
        Optional<ProductStatusDetail> productStatusDetailOptional = productStatusDetailRepo.findProductStatusDetailByIdAndActiveFlag(productStatusDetailId, 1);
        if (!productStatusDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusDetail with " + productStatusDetailId + " not found!");
        }
        ProductStatusDetail productStatusDetail = productStatusDetailOptional.get();
        productStatusDetail.setActiveFlag(0);
        productStatusDetailRepo.save(productStatusDetail);

        if (!isDeletedList) {
            ProductStatusList productStatusList = productStatusDetail.getProductStatusList();
            productStatusList.setPrice(productStatusList.getPrice().subtract(productStatusDetail.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetail.getQty()))));
            productStatusListRepo.save(productStatusList);
        }

        if (productStatusDetail.getProductStatusList().getType() == Constant.PRODUCT_DONE) {
            Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(productStatusDetail.getProductInfo().getId(), 1);
            if (!productInfoOptional.isPresent()) {
                throw new ResourceNotFoundException("Product Info with ID: " + productStatusDetail.getProductInfo().getId() + " not found!");
            }
            ProductInfo productInfo = productInfoOptional.get();
            int oldQty = productInfo.getQty();
            productInfo.setQty(oldQty - productStatusDetail.getQty());
            if (productInfo.getQty() == 0) {
                productInfo.setPriceIn(new BigDecimal("1"));
                productInfo.setPriceOut(new BigDecimal("1"));
            } else {
                productInfo.setPriceIn(productInfo.getPriceIn().multiply(BigDecimal.valueOf(oldQty)).subtract(productStatusDetail.getPriceOne().multiply(BigDecimal.valueOf(productStatusDetail.getQty()))).divide(BigDecimal.valueOf(productInfo.getQty()), 2, RoundingMode.HALF_UP));
                productInfo.setPriceOut(productInfo.getPriceIn().add(productInfo.getPriceIn().multiply(BigDecimal.valueOf(0.2))));
            }
            productInfoRepo.save(productInfo);

            productDetailRepo.findProductDetailsByProductStatusList(productStatusDetail.getProductStatusList().getId()).forEach(productDetail -> {
                try {
                    productDetailService.delete(productDetail.getId(), true);
                } catch (ResourceNotFoundException | LogicException e) {
                    e.printStackTrace();
                }
            });
        }


    }

    @Override
    public Map<String, Long> getCountRecord() {
        Map<String, Long> mapCount = new HashMap<>();
        mapCount.put("LIST_DONE", productStatusListRepo.countProductStatusListByTypeAndActiveFlag(Constant.PRODUCT_DONE));
        mapCount.put("LIST_BACK", productStatusListRepo.countProductStatusListByTypeAndActiveFlag(Constant.PRODUCT_BACK));
        mapCount.put("DETAIL_DONE", productStatusDetailRepo.countProductStatusDetailByTypeAndActiveFlag(Constant.PRODUCT_DONE));
        mapCount.put("DETAIL_BACK", productStatusDetailRepo.countProductStatusDetailByTypeAndActiveFlag(Constant.PRODUCT_BACK));

        return mapCount;
    }
}
