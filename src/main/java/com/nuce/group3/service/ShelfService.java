package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ShelfRequest;
import com.nuce.group3.controller.dto.response.ShelfResponse;
import com.nuce.group3.data.model.Shelf;
import com.nuce.group3.exception.LogicException;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShelfService {
    List<ShelfResponse> getAll(Integer page, Integer size);

    List<ShelfResponse> findShelfByFilter(String name, int qtyFrom, int qtyTo,  int qtyRestFrom,  int qtyRestTo, String branchName, Integer page, Integer size);

    void save(ShelfRequest shelfRequest) throws LogicException, ResourceNotFoundException;

    ShelfResponse edit(Integer supplierId, ShelfRequest shelfRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer supplierId) throws ResourceNotFoundException;

    ShelfResponse findShelfById(Integer supplierId) throws ResourceNotFoundException;

}
