package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductStatusListRequest;
import com.nuce.group3.controller.dto.response.ProductStatusListResponse;
import com.nuce.group3.data.model.ProductStatusList;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.data.model.ProductStatusList;
import com.nuce.group3.data.model.Vat;
import com.nuce.group3.data.repo.UserRepo;
import com.nuce.group3.data.repo.ProductStatusListRepo;
import com.nuce.group3.data.repo.VatRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.ProductStatusListService;
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
public class ProductStatusListServiceImpl implements ProductStatusListService {

    @Autowired
    private VatRepo vatRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private ProductStatusListRepo productStatusListRepo;

    @Override
    public List<ProductStatusListResponse> getAll(Integer page, Integer size) {
        List<ProductStatusListResponse> productStatusListResponses = new ArrayList<>();
        if (page==null) page = 0;
        if (size==null) size = 5;
        productStatusListRepo.findProductStatusListByActiveFlag(1, PageRequest.of(page,size)).forEach(productStatusList -> {
            ProductStatusListResponse productStatusListResponse = ProductStatusListResponse.builder()
                    .code(productStatusList.getCode())
                    .vatCode(productStatusList.getVat().getCode())
                    .userName(productStatusList.getUser().getName())
                    .price(productStatusList.getPrice())
                    .createDate(productStatusList.getCreateDate())
                    .updateDate(productStatusList.getUpdateDate())
                    .build();
            productStatusListResponses.add(productStatusListResponse);
        });
        return productStatusListResponses;
    }

    @Override
    public List<ProductStatusListResponse> findProductStatusListByFilter(String code, String vatCode, BigDecimal priceFrom, BigDecimal priceTo, int type, Integer page, Integer size) {
        List<ProductStatusListResponse> productStatusListResponses = new ArrayList<>();
        if (page==null) page = 0;
        if (size==null) size = 5;
        productStatusListRepo.findProductStatusListByFilter(code, vatCode, priceFrom, priceTo, type,PageRequest.of(page, size)).forEach(productStatusList -> {
            ProductStatusListResponse productStatusListResponse = ProductStatusListResponse.builder()
                    .code(productStatusList.getCode())
                    .vatCode(productStatusList.getVat().getCode())
                    .userName(productStatusList.getUser().getName())
                    .price(productStatusList.getPrice())
                    .createDate(productStatusList.getCreateDate())
                    .updateDate(productStatusList.getUpdateDate())
                    .build();
            productStatusListResponses.add(productStatusListResponse);
        });
        return productStatusListResponses;
    }

    @Override
    public ProductStatusListResponse findProductStatusListById(Integer productStatusListId) throws ResourceNotFoundException {
        if (productStatusListId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByIdAndActiveFlag(productStatusListId,1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusList with " + productStatusListId + " not found!");
        }
        ProductStatusList productStatusList = productStatusListOptional.get();
        return ProductStatusListResponse.builder()
                .code(productStatusList.getCode())
                .vatCode(productStatusList.getVat().getCode())
                .userName(productStatusList.getUser().getName())
                .price(productStatusList.getPrice())
                .createDate(productStatusList.getCreateDate())
                .updateDate(productStatusList.getUpdateDate())
                .build();
    }

    @Override
    public void save(ProductStatusListRequest productStatusListRequest, String userName) throws LogicException, ResourceNotFoundException {
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByCodeAndActiveFlag(productStatusListRequest.getCode(), 1);
        if (productStatusListOptional.isPresent()) {
            throw new LogicException("ProductStatusList Existed", HttpStatus.BAD_REQUEST);
        }
        Optional<Vat> vatOptional = vatRepo.findVatByIdAndActiveFlag(productStatusListRequest.getVatId(), 1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("Vat with id " + productStatusListRequest.getVatId() + " not found");
        }

        Optional<Users> usersOptional = userRepo.findUsersByUserName(userName);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with user name: " +  userName+ " not found");
        }

        ProductStatusList productStatusList = new ProductStatusList();
        productStatusList.setCode(productStatusListRequest.getCode());
        productStatusList.setPrice(new BigDecimal(0));
        productStatusList.setUser(usersOptional.get());
        productStatusList.setVat(vatOptional.get());
        productStatusList.setType(productStatusListRequest.getType());
        productStatusList.setActiveFlag(1);
        productStatusList.setCreateDate(new Date());
        productStatusList.setUpdateDate(new Date());
        productStatusListRepo.save(productStatusList);
    }

    @Override
    public ProductStatusListResponse edit(Integer productStatusListId, ProductStatusListRequest productStatusListRequest, String userName) throws ResourceNotFoundException, LogicException {
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByIdAndActiveFlag(productStatusListId, 1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusList with " + productStatusListId + " not found!");
        }
        ProductStatusList productStatusList = productStatusListOptional.get();
        Optional<Vat> vatOptional = vatRepo.findVatByIdAndActiveFlag(productStatusListRequest.getVatId(), 1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("Vat with id " + productStatusListRequest.getVatId() + " not found");
        }

        Optional<Users> usersOptional = userRepo.findUsersByUserName(userName);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with user name: " +  userName+ " not found");
        }

        Optional<ProductStatusList> productStatusListByCode = productStatusListRepo.findProductStatusListByCodeAndActiveFlag(productStatusListRequest.getCode(), 1);
        if (productStatusListByCode.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusList with code" + productStatusListRequest.getCode() + " existed!");
        }

        productStatusList.setCode(productStatusListRequest.getCode());
        productStatusList.setVat(vatOptional.get());
        productStatusList.setUser(usersOptional.get());
        productStatusList.setUpdateDate(new Date());
        try {
            productStatusListRepo.save(productStatusList);
            return ProductStatusListResponse.builder()
                    .code(productStatusList.getCode())
                    .vatCode(productStatusList.getVat().getCode())
                    .userName(productStatusList.getUser().getName())
                    .price(productStatusList.getPrice())
                    .createDate(productStatusList.getCreateDate())
                    .updateDate(productStatusList.getUpdateDate())
                    .build();
        } catch (Exception e) {
            throw new LogicException("Edit error", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Integer productStatusListId) throws ResourceNotFoundException {
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByIdAndActiveFlag(productStatusListId,1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusList with " + productStatusListId + " not found!");
        }
        productStatusListOptional.get().setActiveFlag(0);
        productStatusListRepo.save(productStatusListOptional.get());
    }
}
