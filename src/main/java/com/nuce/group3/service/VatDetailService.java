package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.VatDetailRequest;
import com.nuce.group3.controller.dto.response.VatDetailResponse;
import com.nuce.group3.exception.LogicException;

import java.math.BigDecimal;
import java.util.List;

public interface VatDetailService {
    List<VatDetailResponse> getAll(Integer page, Integer size);

    List<VatDetailResponse> findVatDetailByFilter(BigDecimal priceTotalFrom, BigDecimal priceTotalTo, String vatCode, String productInfo, Integer page, Integer size);

    void save(VatDetailRequest vatDetailRequest) throws LogicException, ResourceNotFoundException;

    VatDetailResponse edit(Integer vatDetailId, VatDetailRequest vatDetailRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer vatDetailId) throws ResourceNotFoundException;

    VatDetailResponse findVatDetailById(Integer vatDetailId) throws ResourceNotFoundException;

}
