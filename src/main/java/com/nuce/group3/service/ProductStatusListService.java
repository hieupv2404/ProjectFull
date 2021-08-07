package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductStatusListRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.ProductStatusListResponse;
import com.nuce.group3.exception.LogicException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ProductStatusListService {
    List<ProductStatusListResponse> getAll(int type, Integer page, Integer size);

    GenericResponse findProductStatusListByFilter(String code, String vatCode, BigDecimal priceFrom, BigDecimal priceTo, int type, Integer branchId, Date dateFrom, Date dateTo, Integer page, Integer size);

    ProductStatusListResponse findProductStatusListById(Integer productStatusListId) throws ResourceNotFoundException;

    void save(Integer vatId, ProductStatusListRequest productStatusListRequest) throws LogicException, ResourceNotFoundException;

    ProductStatusListResponse edit(Integer productStatusListId, ProductStatusListRequest productStatusListRequest, String userName) throws ResourceNotFoundException, LogicException;

    void delete(Integer productStatusListId) throws ResourceNotFoundException, LogicException;
}
