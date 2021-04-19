package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.SupplierRequest;
import com.nuce.group3.controller.dto.response.SupplierResponse;
import com.nuce.group3.data.model.Supplier;
import com.nuce.group3.exception.LogicException;

import java.util.List;

public interface SupplierService {
    List<Supplier> getAll(Integer page, Integer size);

    List<Supplier> findSupplierByFilter(String name, String phone, String address, Integer page, Integer size);

    void save(SupplierRequest supplierRequest) throws LogicException, ResourceNotFoundException;

    Supplier edit(Integer supplierId, SupplierRequest supplierRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer supplierId) throws ResourceNotFoundException;
    
    Supplier findSupplierById(Integer supplierId) throws ResourceNotFoundException;
}
