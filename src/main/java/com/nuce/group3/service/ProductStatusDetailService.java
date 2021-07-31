package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductStatusDetailRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.ProductStatusDetailResponse;
import com.nuce.group3.exception.LogicException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductStatusDetailService {
    List<ProductStatusDetailResponse> getAll(int type, Integer page, Integer size);

    GenericResponse findProductStatusDetailByFilter(BigDecimal priceTotalFrom, BigDecimal priceTotalTo, String productStatusListCode, String productInfo, int type, Integer branchId, Integer page, Integer size);

    void save(ProductStatusDetailRequest productStatusDetailRequest) throws LogicException, ResourceNotFoundException;

    ProductStatusDetailResponse edit(Integer productStatusDetailId, ProductStatusDetailRequest productStatusDetailRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer productStatusDetailId, boolean isDeletedList) throws ResourceNotFoundException;

    ProductStatusDetailResponse findProductStatusDetailById(Integer productStatusDetailId) throws ResourceNotFoundException;

    List<ProductStatusDetailResponse> findProductStatusDetailToExport(BigDecimal priceTotalFrom, BigDecimal priceTotalTo, String productStatusListCode, String productInfo, Integer type, Integer branchId);

    Map<String, Long> getCountRecord();


}
