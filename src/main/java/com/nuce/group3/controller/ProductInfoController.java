package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.ProductInfoRequest;
import com.nuce.group3.controller.dto.response.ProductInfoResponse;
import com.nuce.group3.controller.dto.response.ProductInfoResponseTest;
import com.nuce.group3.data.model.ProductInfo;
import com.nuce.group3.data.repo.ProductInfoRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.ProductInfoService;
import com.nuce.group3.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/products-info", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class ProductInfoController {
    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private ProductInfoRepo productInfoRepo;

    @GetMapping
    @HasRole({"ADMIN","ADMIN_PTTK"})
    public ResponseEntity<List<ProductInfoResponse>> findProductInfo(@RequestParam(name = "name", required = false) String name,
                                                                     @RequestParam(name = "categoryName", required = false) String categoryName,
                                                                     @RequestParam(name = "qtyFrom", required = false) int qtyFrom,
                                                                     @RequestParam(name = "qtyTo", required = false) int qtyTo,
                                                                     @RequestParam(name = "priceFrom", required = false) BigDecimal priceFrom,
                                                                     @RequestParam(name = "priceTo", required = false) BigDecimal priceTo) {
        return new ResponseEntity<>(productInfoService.findProductInfoByFilter(name, categoryName, qtyFrom, qtyTo, priceFrom, priceTo), HttpStatus.OK);

    }

    @PostMapping
    @HasRole({"ADMIN","ADMIN_PTTK"})
    public ResponseEntity<String> createProductInfo(@Valid @RequestBody ProductInfoRequest productInfoRequest
            , @RequestParam("image") MultipartFile multipartFile) throws IOException, ResourceNotFoundException, LogicException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        productInfoRequest.setImgUrl(fileName);
        String uploadDir = "product-info-img/";

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        productInfoService.save(productInfoRequest);

        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    @HasRole({"ADMIN","ADMIN_PTTK"})
    public  ResponseEntity<ProductInfoResponse> findById (@PathVariable Integer productId) throws ResourceNotFoundException {
        return new ResponseEntity<>(productInfoService.findProductInfoById(productId), HttpStatus.OK);
    }

    @GetMapping("/test")
    @HasRole({"ADMIN","ADMIN_PTTK"})
    public  ResponseEntity<List<ProductInfoResponseTest>> test () throws ResourceNotFoundException {
        return new ResponseEntity<>(productInfoRepo.test(), HttpStatus.OK);
    }
}
