package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ShelfRequest;
import com.nuce.group3.controller.dto.response.ShelfResponse;
import com.nuce.group3.exception.LogicException;

import java.util.List;

public interface ShelfService {
    List<ShelfResponse> getAll(Integer page, Integer size);

    List<ShelfResponse> findShelfByFilter(String name, int qtyFrom, int qtyTo,  int qtyRestFrom,  int qtyRestTo, String branchName, Integer page, Integer size);

    void save(ShelfRequest shelfRequest, int branchId) throws LogicException, ResourceNotFoundException;

    ShelfResponse edit(Integer shelfId, ShelfRequest shelfRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer shelfId) throws ResourceNotFoundException;

    ShelfResponse findShelfById(Integer shelfId) throws ResourceNotFoundException;

}
