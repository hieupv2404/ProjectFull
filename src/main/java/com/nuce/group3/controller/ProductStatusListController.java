package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.ProductStatusListDetailRequest;
import com.nuce.group3.controller.dto.request.ProductStatusListRequest;
import com.nuce.group3.controller.dto.response.ProductStatusListResponse;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.ProductStatusListDetailService;
import com.nuce.group3.service.ProductStatusListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/productStatusLists", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class ProductStatusListController {
    @Autowired
    private ProductStatusListService productStatusListService;
    

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<List<ProductStatusListResponse>> findProductStatusList(@RequestParam(name = "code", required = false) String code,
                                                     @RequestParam(name = "vatCode", required = false) String vatCode,
                                                     @RequestParam(name = "supplierName", required = false) String supplierName,
                                                     @RequestParam(name = "userName", required = false) String userName,
                                                     @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(productStatusListService.findProductStatusListByFilter(code, tax, supplierName, userName, page, size), HttpStatus.OK);

    }

    @PostMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createProductStatusList(@Valid @RequestBody ProductStatusListRequest productStatusListRequest, HttpServletRequest request) throws ResourceNotFoundException, LogicException {
        productStatusListService.save(productStatusListRequest, String.valueOf(request.getSession().getAttribute("Username")));
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{productStatusListId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<ProductStatusListResponse> findById(@PathVariable Integer productStatusListId) throws ResourceNotFoundException {
        return new ResponseEntity<>(productStatusListService.findProductStatusListById(productStatusListId), HttpStatus.OK);
    }

    @PutMapping("/edit/{productStatusListId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<ProductStatusListResponse> editProductStatusList(@PathVariable Integer productStatusListId, @Valid @RequestBody ProductStatusListRequest productStatusListRequest, HttpServletRequest request) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(productStatusListService.edit(productStatusListId, productStatusListRequest, String.valueOf(request.getSession().getAttribute("Username"))), HttpStatus.OK);
    }

    @PutMapping("/delete/{productStatusListId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> deleteProductStatusList(@PathVariable Integer productStatusListId) throws ResourceNotFoundException {
        productStatusListService.delete(productStatusListId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @PostMapping("/{productStatusListId}/add-items")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createProductStatusListDetail(@PathVariable Integer productStatusListId,@Valid @RequestBody ProductStatusListDetailRequest productStatusListDetailRequest) throws ResourceNotFoundException, LogicException {
        productStatusListDetailRequest.setProductStatusListId(productStatusListId);
        productStatusListDetailService.save(productStatusListDetailRequest);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }
}
