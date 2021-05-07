package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.VatRequest;
import com.nuce.group3.controller.dto.response.VatResponse;
import com.nuce.group3.exception.LogicException;

import java.util.List;

public interface VatService {
    List<VatResponse> getAll(Integer page, Integer size);

    List<VatResponse> findVatByFilter(String code, String tax, String supplierName, String userName, Integer page, Integer size);

    VatResponse findVatById(Integer vatId) throws ResourceNotFoundException;

    void save(VatRequest vatRequest, String userName) throws LogicException, ResourceNotFoundException;

    VatResponse edit(Integer vatId, VatRequest vatRequest, String userName) throws ResourceNotFoundException, LogicException;

    void delete(Integer vatId) throws ResourceNotFoundException;
}