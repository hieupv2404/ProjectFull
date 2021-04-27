package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.VatRequest;
import com.nuce.group3.controller.dto.response.VatResponse;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.VatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/vats", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class VatController {
    @Autowired
    private VatService vatService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<List<VatResponse>> findVat(@RequestParam(name = "code", required = false) String code,
                                                     @RequestParam(name = "tax", required = false) String tax,
                                                     @RequestParam(name = "supplierName", required = false) String supplierName,
                                                     @RequestParam(name = "userName", required = false) String userName,
                                                     @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(vatService.findVatByFilter(code, tax, supplierName, userName, page, size), HttpStatus.OK);

    }

    @PostMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createVat(@Valid @RequestBody VatRequest vatRequest, HttpServletRequest request) throws ResourceNotFoundException, LogicException {
        vatService.save(vatRequest, String.valueOf(request.getSession().getAttribute("Username")));
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{vatId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<VatResponse> findById(@PathVariable Integer vatId) throws ResourceNotFoundException {
        return new ResponseEntity<>(vatService.findVatById(vatId), HttpStatus.OK);
    }

    @PutMapping("/edit/{vatId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<VatResponse> editVat(@PathVariable Integer vatId, @Valid @RequestBody VatRequest vatRequest, HttpServletRequest request) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(vatService.edit(vatId, vatRequest, String.valueOf(request.getSession().getAttribute("Username"))), HttpStatus.OK);
    }

    @PutMapping("/delete/{vatId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> deleteVat(@PathVariable Integer vatId) throws ResourceNotFoundException {
        vatService.delete(vatId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }
}
