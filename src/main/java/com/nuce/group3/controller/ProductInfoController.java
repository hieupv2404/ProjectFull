package com.nuce.group3.controller;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.nuce.group3.controller.dto.request.ProductInfoRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.ProductInfoResponse;
import com.nuce.group3.data.repo.ProductInfoRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.ProductInfoService;
import com.nuce.group3.utils.Constant;
import com.nuce.group3.utils.googledrive.CreateGoogleFile;
import com.nuce.group3.utils.googledrive.GoogleDriveUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/api/products-info", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@Slf4j
public class ProductInfoController {
    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private ProductInfoRepo productInfoRepo;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<GenericResponse> findProductInfo(@RequestParam(name = "name", required = false) String name,
                                                           @RequestParam(name = "categoryName", required = false) String categoryName,
                                                           @RequestParam(name = "qtyFrom", required = false) Integer qtyFrom,
                                                           @RequestParam(name = "qtyTo", required = false) Integer qtyTo,
                                                           @RequestParam(name = "priceFrom", required = false) BigDecimal priceFrom,
                                                           @RequestParam(name = "priceTo", required = false) BigDecimal priceTo,
                                                           @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(productInfoService.findProductInfoByFilter(name, categoryName, qtyFrom, qtyTo, priceFrom, priceTo, page - 1, size), HttpStatus.OK);
    }

    @PostMapping(consumes = {"application/json",
            "multipart/form-data"}, produces = "application/json")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createProductInfo(@RequestParam("image") MultipartFile multipartFile, @RequestParam("name") String name, @RequestParam("description") String description, @RequestParam("categoryId") Integer categoryId) throws IOException, ResourceNotFoundException, LogicException {
        String fileName = multipartFile.getOriginalFilename();
        ProductInfoRequest productInfoRequest = new ProductInfoRequest();
        try {
            File directory = new File(String.valueOf(Constant.UPLOAD_PATH));
            File file = new File(fileName);
            if (!directory.exists()) {

                directory.mkdir();
                if (!file.exists()) {
                    file.getParentFile().mkdir();
                    file.createNewFile();
                }
            }

            FileCopyUtils.copy(multipartFile.getBytes(), new File(Constant.UPLOAD_PATH + fileName));

            File uploadFile = new File(Constant.UPLOAD_PATH + fileName);

            // Create Google File:

            com.google.api.services.drive.model.File googleFile = CreateGoogleFile.createGoogleFile(null, "image/*", fileName, uploadFile);

            log.info("Created Google file!");
            log.info("WebContentLink: " + googleFile.getWebContentLink());
            log.info("WebViewLink: " + googleFile.getWebViewLink());
            log.info("ID CODE: " + googleFile.getId());
            log.info("Done!");

            String permissionType = "anyone";
            // All values: organizer - owner - writer - commenter - reader
            String permissionRole = "writer";
            Permission newPermission = new Permission();
            newPermission.setType(permissionType);
            newPermission.setRole(permissionRole);

            Drive driveService = GoogleDriveUtils.getDriveService();
            driveService.permissions().create(googleFile.getId(), newPermission).execute();

            productInfoRequest.setImgName(fileName);
            productInfoRequest.setName(name);
            productInfoRequest.setDescription(description);
            productInfoRequest.setCategoryId(categoryId);
            productInfoRequest.setImgUrl("https://drive.google.com/uc?id=" + googleFile.getId());
            productInfoService.save(productInfoRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @PutMapping(value = "/edit/{productId}", consumes = {"application/json",
            "multipart/form-data"}, produces = "application/json")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<ProductInfoResponse> editProductInfo(@PathVariable Integer productId, @RequestParam("image") MultipartFile multipartFile, @RequestParam("name") String name, @RequestParam("description") String description, @RequestParam("categoryId") Integer categoryId) throws IOException, ResourceNotFoundException, LogicException {
        String fileName = multipartFile.getOriginalFilename();
        ProductInfoRequest productInfoRequest = new ProductInfoRequest();
        try {
            File directory = new File(String.valueOf(Constant.UPLOAD_PATH));
            File file = new File(fileName);
            if (!directory.exists()) {

                directory.mkdir();
                if (!file.exists()) {
                    file.getParentFile().mkdir();
                    file.createNewFile();
                }
            }

            FileCopyUtils.copy(multipartFile.getBytes(), new File(Constant.UPLOAD_PATH + fileName));

            File uploadFile = new File(Constant.UPLOAD_PATH + fileName);

            // Create Google File:

            com.google.api.services.drive.model.File googleFile = CreateGoogleFile.createGoogleFile(null, "image/*", fileName, uploadFile);

            log.info("Created Google file!");
            log.info("WebContentLink: " + googleFile.getWebContentLink());
            log.info("WebViewLink: " + googleFile.getWebViewLink());
            log.info("ID CODE: " + googleFile.getId());
            log.info("Done!");

            String permissionType = "anyone";
            // All values: organizer - owner - writer - commenter - reader
            String permissionRole = "writer";
            Permission newPermission = new Permission();
            newPermission.setType(permissionType);
            newPermission.setRole(permissionRole);

            Drive driveService = GoogleDriveUtils.getDriveService();
            driveService.permissions().create(googleFile.getId(), newPermission).execute();

            productInfoRequest.setImgName(fileName);
            productInfoRequest.setName(name);
            productInfoRequest.setDescription(description);
            productInfoRequest.setCategoryId(categoryId);
            productInfoRequest.setImgUrl("https://drive.google.com/uc?id=" + googleFile.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(productInfoService.edit(productId, productInfoRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{productId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> editProductInfo(@PathVariable Integer productId) throws ResourceNotFoundException {
        productInfoService.delete(productId);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<ProductInfoResponse> findById(@PathVariable Integer productId) throws ResourceNotFoundException {
        return new ResponseEntity<>(productInfoService.findProductInfoById(productId), HttpStatus.OK);
    }
}
