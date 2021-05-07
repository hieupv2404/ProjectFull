package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.VatDetailRequest;
import com.nuce.group3.controller.dto.response.VatDetailResponse;
import com.nuce.group3.data.model.*;
import com.nuce.group3.data.repo.*;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.VatDetailService;
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
@Transactional
public class VatDetailServiceImpl implements VatDetailService {

    @Autowired
    private VatRepo vatRepo;
    
    @Autowired
    private ProductInfoRepo productInfoRepo;
    
    @Autowired
    private VatDetailRepo vatDetailRepo;

    @Override
    public List<VatDetailResponse> getAll(Integer page, Integer size) {
        List<VatDetailResponse> vatDetailResponses = new ArrayList<>();
        if (page==null) page = 0;
        if (size==null) size = 5;
        vatDetailRepo.findVatDetailByActiveFlag(1, PageRequest.of(page,size)).forEach(vatDetail -> {
            VatDetailResponse vatDetailResponse = VatDetailResponse.builder()
                    .priceTotal(vatDetail.getPriceOne().multiply(BigDecimal.valueOf(vatDetail.getQty())))
                    .priceOne(vatDetail.getPriceOne())
                    .productInfo(vatDetail.getProductInfo().getName())
                    .qty(vatDetail.getQty())
                    .vatCode(vatDetail.getVat().getCode())
                    .build();
            vatDetailResponses.add(vatDetailResponse);
        });
        return vatDetailResponses;
    }

    @Override
    public List<VatDetailResponse> findVatDetailByFilter(BigDecimal priceTotalFrom, BigDecimal priceTotalTo, String vatCode, String productInfo, Integer page, Integer size) {
        List<VatDetailResponse> vatDetailResponses = new ArrayList<>();
        if (page==null) page = 0;
        if (size==null) size = 5;
        vatDetailRepo.findVatDetailByFilter(priceTotalFrom, priceTotalTo, vatCode, productInfo, PageRequest.of(page, size)).forEach(vatDetail -> {
            VatDetailResponse vatDetailResponse = VatDetailResponse.builder()
                    .priceTotal(vatDetail.getPriceOne().multiply(BigDecimal.valueOf(vatDetail.getQty())))
                    .priceOne(vatDetail.getPriceOne())
                    .productInfo(vatDetail.getProductInfo().getName())
                    .qty(vatDetail.getQty())
                    .vatCode(vatDetail.getVat().getCode())
                    .build();
            vatDetailResponses.add(vatDetailResponse);
        });
        return vatDetailResponses;
    }

    @Override
    public VatDetailResponse findVatDetailById(Integer vatDetailId) throws ResourceNotFoundException {
        if (vatDetailId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<VatDetail> vatDetailOptional = vatDetailRepo.findVatDetailByIdAndActiveFlag(vatDetailId,1);
        if (!vatDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Vat Detail with " + vatDetailId + " not found!");
        }
        VatDetail vatDetail = vatDetailOptional.get();
        return VatDetailResponse.builder()
                .priceTotal(vatDetail.getPriceOne().multiply(BigDecimal.valueOf(vatDetail.getQty())))
                .priceOne(vatDetail.getPriceOne())
                .productInfo(vatDetail.getProductInfo().getName())
                .qty(vatDetail.getQty())
                .vatCode(vatDetail.getVat().getCode())
                .build();
    }

    @Override
    public void save(VatDetailRequest vatDetailRequest) throws LogicException, ResourceNotFoundException {
        Optional<VatDetail> vatDetailOptional = vatDetailRepo.findVatDetailByVatAndProduct(vatDetailRequest.getVatId(), vatDetailRequest.getProductId());
        if (vatDetailOptional.isPresent()) {
            throw new LogicException("Vat Detail Existed", HttpStatus.BAD_REQUEST);
        }
        Optional<Vat> vatOptional = vatRepo.findVatByIdAndActiveFlag(vatDetailRequest.getVatId(), 1);
        if (!vatOptional.isPresent()) {
            throw new ResourceNotFoundException("Vat with id " + vatDetailRequest.getVatId() + " not found");
        }

        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(vatDetailRequest.getProductId(),1);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Info with id: " + vatDetailRequest.getProductId()+ " not found");
        }

        VatDetail vatDetail = new VatDetail();
        vatDetail.setVat(vatOptional.get());
        vatDetail.setProductInfo(productInfoOptional.get());
        vatDetail.setQty(vatDetailRequest.getQty());
        vatDetail.setPriceOne(vatDetailRequest.getPriceOne());
        vatDetail.setActiveFlag(1);
        vatDetailRepo.save(vatDetail);
    }

    @Override
    public VatDetailResponse edit(Integer vatDetailId, VatDetailRequest vatDetailRequest) throws ResourceNotFoundException, LogicException {
        Optional<VatDetail> vatDetailOptional = vatDetailRepo.findVatDetailByIdAndActiveFlag(vatDetailId, 1);
        if (!vatDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("VatDetail with ID: " + vatDetailId + " not found!");
        }
        VatDetail vatDetail = vatDetailOptional.get();
        Optional<Vat> vatOptional = vatRepo.findVatByIdAndActiveFlag(vatDetailRequest.getVatId(), 1);
        if (!vatOptional.isPresent()) {
            throw new ResourceNotFoundException("Vat with id " + vatDetailRequest.getVatId() + " not found");
        }

        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(vatDetailRequest.getProductId(),1);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Info with id: " + vatDetailRequest.getProductId()+ " not found");
        }

        Optional<VatDetail> vatDetailOptional2 = vatDetailRepo.findVatDetailByVatAndProduct(vatDetailRequest.getVatId(), vatDetailRequest.getProductId());
        if (vatDetailOptional2.isPresent()) {
            throw new LogicException("Vat Detail Existed", HttpStatus.BAD_REQUEST);
        }

        vatDetail.setVat(vatOptional.get());
        vatDetail.setProductInfo(productInfoOptional.get());
        vatDetail.setQty(vatDetailRequest.getQty());
        vatDetail.setPriceOne(vatDetailRequest.getPriceOne());
        try {
            vatDetailRepo.save(vatDetail);
            return VatDetailResponse.builder()
                    .priceTotal(vatDetail.getPriceOne().multiply(BigDecimal.valueOf(vatDetail.getQty())))
                    .priceOne(vatDetail.getPriceOne())
                    .productInfo(vatDetail.getProductInfo().getName())
                    .qty(vatDetail.getQty())
                    .vatCode(vatDetail.getVat().getCode())
                    .build();
        } catch (Exception e) {
            throw new LogicException("Edit error", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Integer vatDetailId) throws ResourceNotFoundException {
        Optional<VatDetail> vatDetailOptional = vatDetailRepo.findVatDetailByIdAndActiveFlag(vatDetailId,1);
        if (!vatDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("VatDetail with " + vatDetailId + " not found!");
        }
        vatDetailOptional.get().setActiveFlag(0);
        vatDetailRepo.save(vatDetailOptional.get());
    }
}