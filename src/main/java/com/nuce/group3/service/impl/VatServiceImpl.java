package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.VatRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.VatResponse;
import com.nuce.group3.data.model.Branch;
import com.nuce.group3.data.model.Supplier;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.data.model.Vat;
import com.nuce.group3.data.repo.*;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.ProductStatusListService;
import com.nuce.group3.service.VatDetailService;
import com.nuce.group3.service.VatService;
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
public class VatServiceImpl implements VatService {

    @Autowired
    private SupplierRepo supplierRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private VatRepo vatRepo;

    @Autowired
    private BranchRepo branchRepo;

    @Autowired
    private VatDetailRepo vatDetailRepo;

    @Autowired
    private VatDetailService vatDetailService;

    @Autowired
    private ProductStatusListRepo productStatusListRepo;

    @Autowired
    private ProductStatusListService productStatusListService;

    @Override
    public List<VatResponse> getAll(Integer page, Integer size) {
        List<VatResponse> vatResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        vatRepo.findVatByActiveFlag(1, PageRequest.of(page, size)).forEach(vat -> {
            VatResponse vatResponse = VatResponse.builder()
                    .code(vat.getCode())
                    .tax(vat.getTax())
                    .percent(vat.getPercent())
                    .price(vat.getPrice())
                    .total(vat.getPrice().add(vat.getPrice().multiply(vat.getPercent())))
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
    public GenericResponse findVatByFilter(String code, String tax, String supplierName, String userName, Integer branchId, Integer page, Integer size) {
        List<VatResponse> vatResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        vatRepo.findVatByFilter(code, tax, supplierName, userName, branchId, PageRequest.of(page, size)).forEach(vat -> {
            VatResponse vatResponse = VatResponse.builder()
                    .id(vat.getId())
                    .code(vat.getCode())
                    .tax(vat.getTax())
                    .percent(vat.getPercent())
                    .price(vat.getPrice())
                    .total(vat.getPrice().add(vat.getPrice().multiply(vat.getPercent())))
                    .createDate(vat.getCreateDate())
                    .userName(vat.getUser().getName())
                    .supplierName(vat.getSupplier().getName())
                    .updateDate(vat.getUpdateDate())
                    .build();
            vatResponses.add(vatResponse);
        });
        return new GenericResponse(vatResponses.size(), vatResponses);
    }

    @Override
    public VatResponse findVatById(Integer vatId) throws ResourceNotFoundException {
        if (vatId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<Vat> vatOptional = vatRepo.findVatByIdAndActiveFlag(vatId,1);
        if (!vatOptional.isPresent()) {
            throw new ResourceNotFoundException("Vat with " + vatId + " not found!");
        }
        Vat vat = vatOptional.get();
        return VatResponse.builder()
                .id(vat.getId())
                .code(vat.getCode())
                .tax(vat.getTax())
                .percent(vat.getPercent())
                .price(vat.getPrice())
                .total(vat.getPrice().add(vat.getPrice().multiply(vat.getPercent())))
                .createDate(vat.getCreateDate())
                .userName(vat.getUser().getName())
                .supplierName(vat.getSupplier().getName())
                .updateDate(vat.getUpdateDate())
                .build();
    }

    @Override
    public void save(VatRequest vatRequest, Integer supplierId) throws LogicException, ResourceNotFoundException {
        Optional<Vat> vatOptional = vatRepo.findVatByCodeAndActiveFlag(vatRequest.getCode(), 1);
        if (vatOptional.isPresent()) {
            throw new LogicException("Vat Existed", HttpStatus.BAD_REQUEST);
        }

        Optional<Supplier> supplierOptional = supplierRepo.findSupplierByIdAndActiveFlag(supplierId, 1);
        if (!supplierOptional.isPresent()) {
            throw new ResourceNotFoundException("Supplier with id " + supplierId + " not found");
        }

        Optional<Branch> branchOptional = branchRepo.findBranchByIdAndActiveFlag(vatRequest.getBranchId(), 1);
        if (!branchOptional.isPresent()) {
            throw new ResourceNotFoundException("Branch with id " + vatRequest.getBranchId() + " not found");
        }

        Optional<Users> usersOptional = userRepo.findUsersByUserName(vatRequest.getUserName());
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with user name: " + vatRequest.getUserName() + " not found");
        }

        Vat vat = new Vat();
        vat.setCode(vatRequest.getCode());
        vat.setPercent(vatRequest.getPercent());
        vat.setSupplier(supplierOptional.get());
        vat.setUser(usersOptional.get());
        vat.setBranch(branchOptional.get());
        vat.setPrice(new BigDecimal(0));
        if(vatRequest.getTax() == null || vatRequest.getTax().equals("")){
            vat.setTax("123456");
        } else vat.setTax(vatRequest.getTax());
        vat.setActiveFlag(1);
        vat.setCreateDate(new Date());
        vat.setUpdateDate(new Date());

        vatRepo.save(vat);
    }

    @Override
    public VatResponse edit(Integer vatId, VatRequest vatRequest) throws ResourceNotFoundException, LogicException {
        Optional<Vat> vatOptional = vatRepo.findVatByIdAndActiveFlag(vatId, 1);
        if (!vatOptional.isPresent()) {
            throw new ResourceNotFoundException("Vat with " + vatId + " not found!");
        }
        Vat vat = vatOptional.get();

        Optional<Users> usersOptional = userRepo.findUsersByUserName(vatRequest.getUserName());
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with user name: " + vatRequest.getUserName() + " not found");
        }

        Optional<Vat> vatByCode = vatRepo.findVatByCodeAndActiveFlag(vatRequest.getCode(), 1);
        if (vatByCode.isPresent()) {
            throw new ResourceNotFoundException("Vat with code" + vatRequest.getCode() + " existed!");
        }

        if(vatRequest.getTax() == null){
            vat.setTax("123456");
        } else vat.setTax(vatRequest.getTax());
        vat.setCode(vatRequest.getCode());
        vat.setPercent(vatRequest.getPercent());
        vat.setUser(usersOptional.get());
        vat.setUpdateDate(new Date());
        try {
            vatRepo.save(vat);
            return VatResponse.builder()
                    .id(vat.getId())
                    .code(vat.getCode())
                    .tax(vat.getTax())
                    .percent(vat.getPercent())
                    .price(vat.getPrice())
                    .total(vat.getPrice().add(vat.getPrice().multiply(vat.getPercent())))
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
        Optional<Vat> vatOptional = vatRepo.findVatByIdAndActiveFlag(vatId, 1);
        if (!vatOptional.isPresent()) {
            throw new ResourceNotFoundException("Vat with " + vatId + " not found!");
        }
        vatOptional.get().setActiveFlag(0);
        vatRepo.save(vatOptional.get());
        vatDetailRepo.findVatDetailByVat(vatId).forEach(vatDetail -> {
            try {
                vatDetailService.delete(vatDetail.getId(), true);
            } catch (ResourceNotFoundException resourceNotFoundException) {
                resourceNotFoundException.printStackTrace();
            }
        });
        productStatusListRepo.findProductStatusListByVat(vatId).forEach(productStatusList -> {
            try {
                productStatusListService.delete(productStatusList.getId());
            } catch (ResourceNotFoundException resourceNotFoundException) {
                resourceNotFoundException.printStackTrace();
            }
        });

    }
}
