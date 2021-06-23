package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.ProductDetailRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.ProductDetailResponse;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping(value="/api/products-detail", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class ProductDetailController {
    @Autowired
    private ProductDetailService productDetailService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<GenericResponse> findProductDetail(@RequestParam(name = "name", required = false) String name,
                                                             @RequestParam(name = "imei", required = false) String imei,
                                                             @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(productDetailService.findProductDetailByFilter(name, imei, page, size), HttpStatus.OK);
    }

    @PostMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createProductDetail(@Valid @RequestBody ProductDetailRequest productDetailRequest) throws IOException, ResourceNotFoundException, LogicException {
        productDetailService.save(productDetailRequest);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<ProductDetailResponse> findById(@PathVariable Integer productId) throws ResourceNotFoundException {
        return new ResponseEntity<>(productDetailService.findProductDetailById(productId), HttpStatus.OK);
    }

    @PutMapping("/edit/{productId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<ProductDetailResponse> editProductDetail(@PathVariable Integer productId, @Valid @RequestBody ProductDetailRequest productDetailRequest) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(productDetailService.edit(productId, productDetailRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{productId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> deleteProductDetail(@PathVariable Integer productId) throws ResourceNotFoundException, LogicException {
        productDetailService.delete(productId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }
}
