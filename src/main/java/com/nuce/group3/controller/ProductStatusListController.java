package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.ProductStatusDetailRequest;
import com.nuce.group3.controller.dto.request.ProductStatusListRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.ProductStatusListResponse;
import com.nuce.group3.data.repo.ProductStatusDetailRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.ProductStatusDetailService;
import com.nuce.group3.service.ProductStatusListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/product-status-lists", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class ProductStatusListController {
    @Autowired
    private ProductStatusListService productStatusListService;

    @Autowired
    private ProductStatusDetailService productStatusDetailService;

    @Autowired
    private ProductStatusDetailRepo productStatusDetailRepo;


    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<GenericResponse> findProductStatusList(@RequestParam(name = "code", required = false) String code,
                                                                 @RequestParam(name = "vatCode", required = false) String vatCode,
                                                                 @RequestParam(name = "priceFrom", required = false) BigDecimal priceFrom,
                                                                 @RequestParam(name = "priceTo", required = false) BigDecimal priceTo,
                                                                 @RequestParam(name = "type", required = false) int type,
                                                                 @RequestParam(name = "branchId", required = false) Integer branchId,
                                                                 @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(productStatusListService.findProductStatusListByFilter(code, vatCode, priceFrom, priceTo, type, branchId, page - 1, size), HttpStatus.OK);

    }

    @GetMapping("/{productStatusListId}")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<ProductStatusListResponse> findById(@PathVariable Integer productStatusListId) throws ResourceNotFoundException {
        return new ResponseEntity<>(productStatusListService.findProductStatusListById(productStatusListId), HttpStatus.OK);
    }

    @PutMapping("/edit/{productStatusListId}")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER"})
    public ResponseEntity<ProductStatusListResponse> editProductStatusList(@PathVariable Integer productStatusListId, @Valid @RequestBody ProductStatusListRequest productStatusListRequest, HttpServletRequest request) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(productStatusListService.edit(productStatusListId, productStatusListRequest, String.valueOf(request.getSession().getAttribute("Username"))), HttpStatus.OK);
    }

    @PutMapping("/delete/{productStatusListId}")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER"})
    public ResponseEntity<String> deleteProductStatusList(@PathVariable Integer productStatusListId) throws ResourceNotFoundException {
        productStatusListService.delete(productStatusListId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @PostMapping("/{productStatusListId}/add-items")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER"})
    public ResponseEntity<String> createProductStatusListDetail(@PathVariable Integer productStatusListId, @Valid @RequestBody ProductStatusDetailRequest productStatusDetailRequest) throws ResourceNotFoundException, LogicException {
        productStatusDetailRequest.setProductStatusListId(productStatusListId);
        productStatusDetailService.save(productStatusDetailRequest);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{productStatusListCode}/product-status-details")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<GenericResponse> findProductStatusDetailByCode(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                                                         @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                                                         @PathVariable(name = "productStatusListCode", required = false) String productStatusListCode,
                                                                         @RequestParam(name = "productInfo", required = false) String productInfo,
                                                                         @RequestParam(name = "type", required = false) int type,
                                                                         @RequestParam(name = "branchId", required = false) Integer branchId,
                                                                         @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(productStatusDetailService.findProductStatusDetailByFilter(priceTotalFrom, priceTotalTo, productStatusListCode, productInfo, branchId, type, page - 1, size), HttpStatus.OK);

    }

    @GetMapping("/get-count-records")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<Map<String, Long>> getCountRecords() {
        return new ResponseEntity<>(productStatusDetailService.getCountRecord(), HttpStatus.OK);
    }
}
