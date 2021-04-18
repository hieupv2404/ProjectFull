package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.SupplierRequest;
import com.nuce.group3.controller.dto.response.ProductDetailResponse;
import com.nuce.group3.controller.dto.response.SupplierResponse;
import com.nuce.group3.data.model.Supplier;
import com.nuce.group3.data.repo.SupplierRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private SupplierRepo supplierRepo;

    @Override
    public List<Supplier> getAll(Integer page, Integer size) {
        if (page==null) page = 0;
        if (size==null) size = 5;
        return supplierRepo.findSupplierByActiveFlag(1, PageRequest.of(page,size));
    }

    @Override
    public List<Supplier> findSupplierByFilter(String name, String phone, String address, Integer page, Integer size) {
        if (page==null) page = 0;
        if (size==null) size = 5;
        return supplierRepo.findSupplierByFilter(name,phone,address, PageRequest.of(page,size));
    }

    @Override
    public void save(SupplierRequest supplierRequest) throws LogicException, ResourceNotFoundException {
        Optional<Supplier> supplierOptional = supplierRepo.findSupplierByNameAndActiveFlag(supplierRequest.getName(),1);
        if(supplierOptional.isPresent())
        {
            throw new LogicException("Supplier with name: " + supplierRequest.getName() +" existed!", HttpStatus.BAD_REQUEST) ;
        }
    }

    @Override
    public Supplier edit(Integer supplierId, SupplierRequest supplierRequest) throws ResourceNotFoundException, LogicException {
        return null;
    }

    @Override
    public void delete(Integer supplierId) throws ResourceNotFoundException {

    }
}
