package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.CustomerRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.data.model.Customer;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.CustomerService;
import com.nuce.group3.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/customers", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private IssueService issueService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<GenericResponse> findCustomer(@RequestParam(name = "name", required = false) String name,
                                                        @RequestParam(name = "phone", required = false) String phone,
                                                        @RequestParam(name = "address", required = false) String address,
                                                        @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(customerService.findCustomerByFilter(name, phone, address, page, size), HttpStatus.OK);
    }

    @PostMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) throws ResourceNotFoundException, LogicException {
        customerService.save(customerRequest);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<Customer> findById(@PathVariable Integer customerId) throws ResourceNotFoundException {
        return new ResponseEntity<>(customerService.findCustomerById(customerId), HttpStatus.OK);
    }

    @PutMapping("/edit/{customerId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<Customer> editCustomer(@PathVariable Integer customerId, @Valid @RequestBody CustomerRequest customerRequest) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(customerService.edit(customerId, customerRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{customerId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> deleteCustomer(@PathVariable Integer customerId) throws ResourceNotFoundException {
        customerService.delete(customerId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @PostMapping("/add-issue/{customerId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createIssue(@PathVariable Integer customerId, HttpServletRequest request) throws ResourceNotFoundException, LogicException {
        issueService.save(customerId, String.valueOf(request.getSession().getAttribute("Username")));
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }
}
