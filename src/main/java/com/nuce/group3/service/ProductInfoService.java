package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductInfoRequest;
import com.nuce.group3.data.model.ProductInfo;
import com.nuce.group3.data.repo.ProductInfoRepo;
import com.nuce.group3.exception.LogicException;
import org.springframework.beans.factory.annotation.Autowired;

public interface ProductInfoService {
    public void save(ProductInfoRequest productInfoRequest) throws LogicException, ResourceNotFoundException;
}
