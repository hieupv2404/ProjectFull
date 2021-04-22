package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.CustomerRequest;
import com.nuce.group3.data.model.Customer;
import com.nuce.group3.data.repo.CustomerRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepo customerRepo;

    @Override
    public List<Customer> getAll(Integer page, Integer size) {
        if (page==null) page = 0;
        if (size==null) size = 5;
        return customerRepo.findCustomerByActiveFlag(1, PageRequest.of(page,size));
    }

    @Override
    public List<Customer> findCustomerByFilter(String name, String phone, String address, Integer page, Integer size) {
        if (page==null) page = 0;
        if (size==null) size = 5;
        return customerRepo.findCustomerByFilter(name,phone,address, PageRequest.of(page,size));
    }

    @Override
    public void save(CustomerRequest customerRequest) throws LogicException, ResourceNotFoundException {
        Optional<Customer> customerOptional = customerRepo.findCustomerByNameAndActiveFlag(customerRequest.getName(),1);
        if(customerOptional.isPresent())
        {
            throw new LogicException("Customer with name: " + customerRequest.getName() +" existed!", HttpStatus.BAD_REQUEST) ;
        }
        Customer customer = new Customer();
        customer.setName(customerRequest.getName());
        customer.setPhone(customerRequest.getPhone());
        customer.setAddress(customerRequest.getAddress());
        customer.setActiveFlag(1);
        customer.setCreateDate(new Date());
        customer.setUpdateDate(new Date());
        customerRepo.save(customer);
    }

    @Override
    public Customer edit(Integer customerId, CustomerRequest customerRequest) throws ResourceNotFoundException, LogicException {
        Optional<Customer> customerOptional = customerRepo.findCustomerByIdAndActiveFlag(customerId,1);
        if(!customerOptional.isPresent())
        {
            throw new ResourceNotFoundException("Customer with ID: " + customerId +" not found!");
        }
        Customer customer = customerOptional.get();
        customer.setName(customerRequest.getName());
        customer.setPhone(customerRequest.getPhone());
        customer.setAddress(customerRequest.getAddress());
        customer.setUpdateDate(new Date());
        return customerRepo.save(customer);
    }

    @Override
    public void delete(Integer customerId) throws ResourceNotFoundException {
        Optional<Customer> customerOptional = customerRepo.findCustomerByIdAndActiveFlag(customerId,1);
        if(!customerOptional.isPresent())
        {
            throw new ResourceNotFoundException("Customer with ID: " + customerId +" not found!");
        }
        customerOptional.get().setActiveFlag(0);
        customerRepo.save(customerOptional.get());
    }

    @Override
    public Customer findCustomerById(Integer customerId) throws ResourceNotFoundException {
        if( customerId == null)
        {
            throw new ResourceNotFoundException("Customer ID is null");
        }
        Optional<Customer> customerOptional = customerRepo.findCustomerByIdAndActiveFlag(customerId, 1);
        if (!customerOptional.isPresent()){
            throw new ResourceNotFoundException("Customer with ID: "+ customerId + " not found!");
        }
        return customerOptional.get();
    }
}
