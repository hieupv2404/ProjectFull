package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductStatusListRequest;
import com.nuce.group3.controller.dto.response.ProductStatusListResponse;
import com.nuce.group3.exception.LogicException;

import java.util.List;

public interface ProductStatusListService {
    List<ProductStatusListResponse> getAll(Integer page, Integer size);

    List<ProductStatusListResponse> findProductStatusListByFilter(String code, String tax, String supplierName, String userName, Integer page, Integer size);

    ProductStatusListResponse findProductStatusListById(Integer vatId) throws ResourceNotFoundException;

    void save(ProductStatusListRequest vatRequest, String userName) throws LogicException, ResourceNotFoundException;

    ProductStatusListResponse edit(Integer vatId, ProductStatusListRequest vatRequest, String userName) throws ResourceNotFoundException, LogicException;

    void delete(Integer vatId) throws ResourceNotFoundException;
}
