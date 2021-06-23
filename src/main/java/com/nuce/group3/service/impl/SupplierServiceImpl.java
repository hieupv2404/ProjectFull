package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.SupplierRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.data.model.Supplier;
import com.nuce.group3.data.repo.SupplierRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
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
    public GenericResponse findSupplierByFilter(String name, String phone, String address, Integer page, Integer size) {
        if (page == null) page = 0;
        if (size == null) size = 5;
        List<Supplier> supplierList = supplierRepo.findSupplierByFilter(name, phone, address, PageRequest.of(page, size));
        return new GenericResponse(supplierList.size(), supplierList);
    }

    @Override
    public void save(SupplierRequest supplierRequest) throws LogicException, ResourceNotFoundException {
        Optional<Supplier> supplierOptional = supplierRepo.findSupplierByNameAndActiveFlag(supplierRequest.getName(),1);
        if(supplierOptional.isPresent())
        {
            throw new LogicException("Supplier with name: " + supplierRequest.getName() +" existed!", HttpStatus.BAD_REQUEST) ;
        }
        Supplier supplier = new Supplier();
        supplier.setName(supplierRequest.getName());
        supplier.setPhone(supplierRequest.getPhone());
        supplier.setAddress(supplierRequest.getAddress());
        supplier.setActiveFlag(1);
        supplier.setCreateDate(new Date());
        supplier.setUpdateDate(new Date());
        supplierRepo.save(supplier);
    }

    @Override
    public Supplier edit(Integer supplierId, SupplierRequest supplierRequest) throws ResourceNotFoundException, LogicException {
        Optional<Supplier> supplierOptional = supplierRepo.findSupplierByIdAndActiveFlag(supplierId,1);
        if(!supplierOptional.isPresent())
        {
            throw new ResourceNotFoundException("Supplier with ID: " + supplierId +" not found!");
        }
        Supplier supplier = supplierOptional.get();
        supplier.setName(supplierRequest.getName());
        supplier.setPhone(supplierRequest.getPhone());
        supplier.setAddress(supplierRequest.getAddress());
        supplier.setUpdateDate(new Date());
        return supplierRepo.save(supplier);
    }

    @Override
    public void delete(Integer supplierId) throws ResourceNotFoundException {
        Optional<Supplier> supplierOptional = supplierRepo.findSupplierByIdAndActiveFlag(supplierId,1);
        if(!supplierOptional.isPresent())
        {
            throw new ResourceNotFoundException("Supplier with ID: " + supplierId +" not found!");
        }
        supplierOptional.get().setActiveFlag(0);
        supplierRepo.save(supplierOptional.get());
    }

    @Override
    public Supplier findSupplierById(Integer supplierId) throws ResourceNotFoundException {
        if( supplierId == null)
        {
            throw new ResourceNotFoundException("Supplier ID is null");
        }
        Optional<Supplier> supplierOptional = supplierRepo.findSupplierByIdAndActiveFlag(supplierId, 1);
        if (!supplierOptional.isPresent()){
            throw new ResourceNotFoundException("Supplier with ID: "+ supplierId + " not found!");
        }
        return supplierOptional.get();
    }
}
