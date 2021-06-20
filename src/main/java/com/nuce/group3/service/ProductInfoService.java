package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductInfoRequest;
import com.nuce.group3.controller.dto.response.ProductInfoResponse;
import com.nuce.group3.exception.LogicException;

import java.math.BigDecimal;
import java.util.List;

public interface ProductInfoService {
    List<ProductInfoResponse> getAll();

    List<ProductInfoResponse> findProductInfoByFilter(String name, String categoryName, Integer qtyFrom, Integer qtyTo, BigDecimal priceFrom, BigDecimal priceTo, Integer page, Integer size);

    ProductInfoResponse findProductInfoById(Integer productId) throws ResourceNotFoundException;

    void save(ProductInfoRequest productInfoRequest) throws LogicException, ResourceNotFoundException;

    ProductInfoResponse edit(Integer productId, ProductInfoRequest productInfoRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer productId) throws ResourceNotFoundException;
}
