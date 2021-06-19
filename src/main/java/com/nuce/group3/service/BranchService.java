package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.BranchRequest;
import com.nuce.group3.controller.dto.response.BranchResponse;
import com.nuce.group3.exception.LogicException;

import java.util.List;

public interface BranchService {
    List<BranchResponse> getAll();

    List<BranchResponse> findBranchByFilter(String name, String code, Integer page, Integer size);

    void save(BranchRequest branchRequest) throws LogicException;

    BranchResponse edit(Integer branchId, BranchRequest branchRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer branchId) throws ResourceNotFoundException;

    BranchResponse findById(Integer branchId) throws ResourceNotFoundException;

    BranchResponse findByCode(String code) throws ResourceNotFoundException;
}
