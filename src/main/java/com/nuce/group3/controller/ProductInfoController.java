package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.ProductInfoRequest;
import com.nuce.group3.data.model.ProductInfo;
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

@RestController
@RequestMapping("/api/products-info")
public class ProductInfoController {
    @Autowired
    private ProductInfoService productInfoService;

    @PostMapping
    public ResponseEntity<String> createProductInfo(@Valid @RequestBody ProductInfoRequest productInfoRequest
            , @RequestParam("image")MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        productInfoRequest.setImgUrl(fileName);
        String uploadDir = "product-info-img/";

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        return new ResponseEntity<>("Created", HttpStatus.OK);
    }
}
