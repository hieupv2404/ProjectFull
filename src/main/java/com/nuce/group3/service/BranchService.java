package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.BranchRequest;
import com.nuce.group3.controller.dto.response.BranchResponse;
import com.nuce.group3.data.model.Branch;
import com.nuce.group3.exception.LogicException;

import java.util.List;

public interface BranchService {
    List<Branch> getAll();

    List<Branch> findBranchByFilter(String name, String code);

    void save(BranchRequest branchRequest) throws LogicException;

    BranchResponse edit(Integer branchId, BranchRequest branchRequest) throws ResourceNotFoundException;

    void delete(Integer branchId) throws ResourceNotFoundException;

    Branch findById(Integer branchId) throws ResourceNotFoundException;
}
