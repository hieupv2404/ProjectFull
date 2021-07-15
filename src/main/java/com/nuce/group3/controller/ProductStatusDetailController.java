package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.ProductStatusDetailRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.ProductStatusDetailResponse;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.ProductStatusDetailExportService;
import com.nuce.group3.service.ProductStatusDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/product-status-details", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class ProductStatusDetailController {
    @Autowired
    private ProductStatusDetailService productStatusDetailService;

    @Autowired
    private ProductStatusDetailExportService productStatusDetailExportService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<GenericResponse> findProductStatusDetail(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                                                   @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                                                   @RequestParam(name = "productStatusListCode", required = false) String productStatusListCode,
                                                                   @RequestParam(name = "productInfo", required = false) String productInfo,
                                                                   @RequestParam(name = "type", required = false) Integer type,
                                                                   @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(productStatusDetailService.findProductStatusDetailByFilter(priceTotalFrom, priceTotalTo, productStatusListCode, productInfo, type, page - 1, size), HttpStatus.OK);

    }

    @GetMapping("/{productStatusDetailId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<ProductStatusDetailResponse> findById(@PathVariable Integer productStatusDetailId) throws ResourceNotFoundException {
        return new ResponseEntity<>(productStatusDetailService.findProductStatusDetailById(productStatusDetailId), HttpStatus.OK);
    }

    @PutMapping("/edit/{productStatusDetailId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<ProductStatusDetailResponse> editProductStatusDetail(@PathVariable Integer productStatusDetailId, @Valid @RequestBody ProductStatusDetailRequest productStatusDetailRequest) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(productStatusDetailService.edit(productStatusDetailId, productStatusDetailRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{productStatusDetailId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> deleteProductStatusDetail(@PathVariable Integer productStatusDetailId) throws ResourceNotFoundException {
        productStatusDetailService.delete(productStatusDetailId, false);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @GetMapping("/get-file-report")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ByteArrayResource getFileReportTest(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                               @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                               @RequestParam(name = "productStatusListCode", required = false) String productStatusListCode,
                                               @RequestParam(name = "productInfo", required = false) String productInfo,
                                               @RequestParam(name = "type", required = false) Integer type) throws IOException {
        List<ProductStatusDetailResponse> productStatusDetailResponses = productStatusDetailService.findProductStatusDetailToExport(priceTotalFrom, priceTotalTo, productStatusListCode, productInfo, type);
        return productStatusDetailExportService.exportReport(productStatusDetailResponses);
    }

    @PostMapping("/export-excel")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<byte[]> exportToExcel(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                                @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                                @RequestParam(name = "productStatusListCode", required = false) String productStatusListCode,
                                                @RequestParam(name = "productInfo", required = false) String productInfo,
                                                @RequestParam(name = "type", required = false) Integer type) throws IOException {
        List<ProductStatusDetailResponse> productStatusDetailResponses = productStatusDetailService.findProductStatusDetailToExport(priceTotalFrom, priceTotalTo, productStatusListCode, productInfo, type);
        ByteArrayResource resource = productStatusDetailExportService.exportReport(productStatusDetailResponses);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource.getByteArray());
    }

}
