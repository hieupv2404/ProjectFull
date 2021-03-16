package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductInfoRequest;
import com.nuce.group3.controller.dto.response.ProductInfoResponse;
import com.nuce.group3.data.model.Category;
import com.nuce.group3.data.model.ProductInfo;
import com.nuce.group3.data.repo.CategoryRepo;
import com.nuce.group3.data.repo.ProductInfoRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class ProductInfoServiceImpl implements ProductInfoService {
    @Autowired
    private ProductInfoRepo productInfoRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Override
    public void save(ProductInfoRequest productInfoRequest) throws LogicException, ResourceNotFoundException {
        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByNameAndActiveFlag(productInfoRequest.getName(),1);
        if(productInfoOptional.isPresent()){
            throw new LogicException("Product Existed", HttpStatus.BAD_REQUEST);
        }
        Optional<Category> categoryOptional = categoryRepo.findById(productInfoRequest.getCategoryId());
        if(!categoryOptional.isPresent())
        {
            throw new ResourceNotFoundException("Categroy with id "+ productInfoRequest.getCategoryId() + " not found");
        }
        ProductInfo productInfo= new ProductInfo();
        productInfo.setCategory(categoryOptional.get());
        productInfo.setName(productInfoRequest.getName());
        productInfo.setDescription(productInfoRequest.getDescription());
        productInfo.setCreateDate(new Date());
        productInfo.setUpdateDate(new Date());
        productInfo.setActiveFlag(1);
        productInfo.setDescription(productInfoRequest.getDescription());
        productInfo.setImgUrl(productInfoRequest.getImgUrl());
        productInfo.setQty(0);
        productInfoRepo.save(productInfo);
    }

}
