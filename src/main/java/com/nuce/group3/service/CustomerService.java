package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.CustomerRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.data.model.Customer;
import com.nuce.group3.exception.LogicException;

import java.util.List;

public interface CustomerService {
    List<Customer> getAll(Integer page, Integer size);

    GenericResponse findCustomerByFilter(String name, String phone, String address, Integer page, Integer size);

    void save(CustomerRequest customerRequest) throws LogicException, ResourceNotFoundException;

    Customer edit(Integer supplierId, CustomerRequest customerRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer supplierId) throws ResourceNotFoundException;
    
    Customer findCustomerById(Integer supplierId) throws ResourceNotFoundException;
}
