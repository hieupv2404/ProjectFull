package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.CategoryRequest;
import com.nuce.group3.controller.dto.request.ProductInfoRequest;
import com.nuce.group3.controller.dto.response.ProductInfoResponse;
import com.nuce.group3.exception.LogicException;

import java.util.List;

public interface ProductInfoService {
    List<ProductInfoResponse> getAll();

    List<ProductInfoResponse> findProductInfoByFilter(String name, String categoryName, int qtyFrom, int qtyTo);

    void save(ProductInfoRequest productInfoRequest) throws LogicException, ResourceNotFoundException;

    ProductInfoResponse edit(Integer productId, ProductInfoRequest productInfoRequest) throws ResourceNotFoundException;

    void delete(Integer productId) throws ResourceNotFoundException;
}
