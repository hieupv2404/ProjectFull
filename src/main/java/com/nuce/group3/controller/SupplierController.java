package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.SupplierRequest;
import com.nuce.group3.controller.dto.request.VatRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.data.model.Supplier;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.SupplierService;
import com.nuce.group3.service.VatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/suppliers", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @Autowired
    private VatService vatService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<GenericResponse> findSupplier(@RequestParam(name = "name", required = false) String name,
                                                        @RequestParam(name = "phone", required = false) String phone,
                                                        @RequestParam(name = "address", required = false) String address,
                                                        @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(supplierService.findSupplierByFilter(name, phone, address, page - 1, size), HttpStatus.OK);

    }

    @PostMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createSupplier(@Valid @RequestBody SupplierRequest supplierRequest) throws ResourceNotFoundException, LogicException {
        supplierService.save(supplierRequest);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{supplierId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<Supplier> findById(@PathVariable Integer supplierId) throws ResourceNotFoundException {
        return new ResponseEntity<>(supplierService.findSupplierById(supplierId), HttpStatus.OK);
    }

    @PutMapping("/edit/{supplierId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<Supplier> editSupplier(@PathVariable Integer supplierId, @Valid @RequestBody SupplierRequest supplierRequest) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(supplierService.edit(supplierId, supplierRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{supplierId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> deleteSupplier(@PathVariable Integer supplierId) throws ResourceNotFoundException, LogicException {
        supplierService.delete(supplierId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @PostMapping("/{supplierId}/add-vat")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createVat(@Valid @RequestBody VatRequest vatRequest, @PathVariable(required = true) Integer supplierId) throws ResourceNotFoundException, LogicException {
        vatService.save(vatRequest, supplierId);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }
}
