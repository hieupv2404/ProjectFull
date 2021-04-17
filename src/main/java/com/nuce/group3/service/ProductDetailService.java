package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductDetailRequest;
import com.nuce.group3.controller.dto.response.ProductDetailResponse;
import com.nuce.group3.exception.LogicException;

import java.util.List;

public interface ProductDetailService {
    List<ProductDetailResponse> getAll();

    List<ProductDetailResponse> findProductDetailByFilter(String name, String supplierName, String imei);

    ProductDetailResponse findProductDetailById(Integer productId) throws ResourceNotFoundException;

    void save(ProductDetailRequest productDetailRequest) throws LogicException, ResourceNotFoundException;

    ProductDetailResponse edit(Integer productId, ProductDetailRequest productDetailRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer productId) throws ResourceNotFoundException;
}
