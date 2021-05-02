package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductStatusListRequest;
import com.nuce.group3.controller.dto.response.ProductStatusListResponse;
import com.nuce.group3.data.model.Supplier;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.data.model.ProductStatusList;
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
        List<ProductStatusListResponse> vatResponses = new ArrayList<>();
        if (page==null) page = 0;
        if (size==null) size = 5;
        vatRepo.findProductStatusListByActiveFlag(1, PageRequest.of(page,size)).forEach(vat -> {
            ProductStatusListResponse vatResponse = ProductStatusListResponse.builder()
                    .code(vat.getCode())
                    .tax(vat.getTax())
                    .percent(vat.getPercent())
                    .price(vat.getPrice())
                    .total(vat.getPrice().multiply(vat.getPercent()))
                    .createDate(vat.getCreateDate())
                    .userName(vat.getUser().getName())
                    .supplierName(vat.getSupplier().getName())
                    .updateDate(vat.getUpdateDate())
                    .build();
            vatResponses.add(vatResponse);
        });
        return vatResponses;
    }

    @Override
    public List<ProductStatusListResponse> findProductStatusListByFilter(String code, String tax, String supplierName, String userName, Integer page, Integer size) {
        List<ProductStatusListResponse> vatResponses = new ArrayList<>();
        if (page==null) page = 0;
        if (size==null) size = 5;
        vatRepo.findProductStatusListByFilter(code, tax, supplierName, userName, PageRequest.of(page, size)).forEach(vat -> {
            ProductStatusListResponse vatResponse = ProductStatusListResponse.builder()
                    .code(vat.getCode())
                    .tax(vat.getTax())
                    .percent(vat.getPercent())
                    .price(vat.getPrice())
                    .total(vat.getPrice().multiply(vat.getPercent()))
                    .createDate(vat.getCreateDate())
                    .userName(vat.getUser().getName())
                    .supplierName(vat.getSupplier().getName())
                    .updateDate(vat.getUpdateDate())
                    .build();
            vatResponses.add(vatResponse);
        });
        return vatResponses;
    }

    @Override
    public ProductStatusListResponse findProductStatusListById(Integer vatId) throws ResourceNotFoundException {
        if (vatId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<ProductStatusList> vatOptional = vatRepo.findProductStatusListByIdAndActiveFlag(vatId,1);
        if (!vatOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusList with " + vatId + " not found!");
        }
        ProductStatusList vat = vatOptional.get();
        return ProductStatusListResponse.builder()
                .code(vat.getCode())
                .tax(vat.getTax())
                .percent(vat.getPercent())
                .price(vat.getPrice())
                .total(vat.getPrice().multiply(vat.getPercent()))
                .createDate(vat.getCreateDate())
                .userName(vat.getUser().getName())
                .supplierName(vat.getSupplier().getName())
                .updateDate(vat.getUpdateDate())
                .build();
    }

    @Override
    public void save(ProductStatusListRequest vatRequest, String userName) throws LogicException, ResourceNotFoundException {
        Optional<ProductStatusList> vatOptional = vatRepo.findProductStatusListByCodeAndActiveFlag(vatRequest.getCode(), 1);
        if (vatOptional.isPresent()) {
            throw new LogicException("ProductStatusList Existed", HttpStatus.BAD_REQUEST);
        }
        Optional<Supplier> supplierOptional = vatRepo1.findSupplierByIdAndActiveFlag(vatRequest.getSupplierId(), 1);
        if (!supplierOptional.isPresent()) {
            throw new ResourceNotFoundException("Supplier with id " + vatRequest.getSupplierId() + " not found");
        }

        Optional<Users> usersOptional = userRepo.findUsersByUserName(userName);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with user name: " +  userName+ " not found");
        }

        ProductStatusList vat = new ProductStatusList();
        vat.setCode(vatRequest.getCode());
        vat.setPercent(vatRequest.getPercent());
        vat.setSupplier(supplierOptional.get());
        vat.setUser(usersOptional.get());
        if(vatRequest.getTax() == null || vatRequest.getTax().equals("")){
            vat.setTax("123456");
        } else vat.setTax(vatRequest.getTax());
        vat.setActiveFlag(1);
        vat.setCreateDate(new Date());
        vat.setUpdateDate(new Date());

        vatRepo.save(vat);
    }

    @Override
    public ProductStatusListResponse edit(Integer vatId, ProductStatusListRequest vatRequest, String userName) throws ResourceNotFoundException, LogicException {
        Optional<ProductStatusList> vatOptional = vatRepo.findProductStatusListByIdAndActiveFlag(vatId, 1);
        if (!vatOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusList with " + vatId + " not found!");
        }
        ProductStatusList vat = vatOptional.get();
        Optional<Supplier> supplierOptional = vatRepo1.findSupplierByIdAndActiveFlag(vatRequest.getSupplierId(), 1);
        if (!supplierOptional.isPresent()) {
            throw new ResourceNotFoundException("Supplier with id " + vatRequest.getSupplierId() + " not found");
        }

        Optional<Users> usersOptional = userRepo.findUsersByUserName(userName);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with user name: " +  userName+ " not found");
        }

        Optional<ProductStatusList> vatByCode = vatRepo.findProductStatusListByCodeAndActiveFlag(vatRequest.getCode(), 1);
        if (vatByCode.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusList with code" + vatRequest.getCode() + " existed!");
        }

        if(vatRequest.getTax() == null){
            vat.setTax("123456");
        } else vat.setTax(vatRequest.getTax());
        vat.setCode(vatRequest.getCode());
        vat.setPercent(vatRequest.getPercent());
        vat.setSupplier(supplierOptional.get());
        vat.setUser(usersOptional.get());
        vat.setUpdateDate(new Date());
        try {
            vatRepo.save(vat);
            return ProductStatusListResponse.builder()
                    .code(vat.getCode())
                    .tax(vat.getTax())
                    .percent(vat.getPercent())
                    .price(vat.getPrice())
                    .total(vat.getPrice().multiply(vat.getPercent()))
                    .createDate(vat.getCreateDate())
                    .userName(vat.getUser().getName())
                    .supplierName(vat.getSupplier().getName())
                    .updateDate(vat.getUpdateDate())
                    .build();
        } catch (Exception e) {
            throw new LogicException("Edit error", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Integer vatId) throws ResourceNotFoundException {
        Optional<ProductStatusList> vatOptional = vatRepo.findProductStatusListByIdAndActiveFlag(vatId,1);
        if (!vatOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusList with " + vatId + " not found!");
        }
        vatOptional.get().setActiveFlag(0);
        vatRepo.save(vatOptional.get());
    }
}
