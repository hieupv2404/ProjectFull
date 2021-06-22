package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.BranchRequest;
import com.nuce.group3.controller.dto.response.BranchResponse;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.data.model.Branch;
import com.nuce.group3.data.repo.BranchRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class BranchServiceImpl implements BranchService {
    @Autowired
    private BranchRepo branchRepo;


    @Override
    public List<BranchResponse> getAll() {
        List<BranchResponse> branchResponses = new ArrayList<>();
        branchRepo.findBranchByActiveFlag(1).forEach(branch -> {
            BranchResponse branchResponse = BranchResponse.builder()
                    .name(branch.getName())
                    .code(branch.getCode())
                    .address(branch.getAddress())
                    .phone(branch.getPhone())
                    .createDate(branch.getCreateDate())
                    .updateDate(branch.getUpdateDate())
                    .build();
            branchResponses.add(branchResponse);
        });
        return branchResponses;
    }

    @Override
    public GenericResponse findBranchByFilter(String name, String code, Integer page, Integer size) {
        List<BranchResponse> branchResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        branchRepo.findBranchByFilter(name, code, PageRequest.of(page, size)).forEach(branch -> {
            BranchResponse branchResponse = BranchResponse.builder()
                    .name(branch.getName())
                    .code(branch.getCode())
                    .address(branch.getAddress())
                    .phone(branch.getPhone())
                    .createDate(branch.getCreateDate())
                    .updateDate(branch.getUpdateDate())
                    .build();
            branchResponses.add(branchResponse);
        });
        return new GenericResponse(branchResponses.size(), branchResponses);
    }

    @Override
    public void save(BranchRequest branchRequest) throws LogicException {
        Optional<Branch> branchOptionalByCode = branchRepo.findBranchByCodeAndActiveFlag(branchRequest.getCode(), 1);
        if (branchOptionalByCode.isPresent()) {
            throw new LogicException("Code is existed!", HttpStatus.BAD_REQUEST);
        }
        Branch branch = new Branch();
        branch.setName(branchRequest.getName());
        branch.setCode(branchRequest.getCode());
        branch.setAddress(branchRequest.getAddress());
        branch.setPhone(branchRequest.getPhone());
        branch.setActiveFlag(1);
        branch.setCreateDate(new Date());
        branch.setUpdateDate(new Date());
        branchRepo.save(branch);
    }

    @Override
    public BranchResponse edit(Integer branchId, BranchRequest branchRequest) throws ResourceNotFoundException, LogicException {
        Optional<Branch> branchOptional = branchRepo.findBranchByIdAndActiveFlag(branchId, 1);
        if (!branchOptional.isPresent())
            throw new ResourceNotFoundException("Branch with id " + branchId + " not found");
        Optional<Branch> branchFindByCodeOptional = branchRepo.findBranchByCodeAndActiveFlag(branchRequest.getCode(), 1);
        if (branchFindByCodeOptional.isPresent())
            throw new LogicException("Branch existed!", HttpStatus.BAD_REQUEST);
        branchOptional.get().setName(branchRequest.getName());
        branchOptional.get().setCode(branchRequest.getCode());
        branchOptional.get().setPhone(branchRequest.getPhone());
        branchOptional.get().setAddress(branchRequest.getAddress());
        branchOptional.get().setUpdateDate(new Date());
        Branch branch = branchRepo.save(branchOptional.get());
        return BranchResponse.builder()
                .name(branch.getName())
                .code(branch.getCode())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .createDate(branch.getCreateDate())
                .updateDate(branch.getUpdateDate())
                .build();
    }

    @Override
    public void delete(Integer branchId) throws ResourceNotFoundException {
        Optional<Branch> branchOptional = branchRepo.findBranchByIdAndActiveFlag(branchId, 1);
        if (!branchOptional.isPresent()) {
            throw new ResourceNotFoundException("Branch with id " + branchId + " not found");
        }
        branchOptional.get().setActiveFlag(0);
        branchRepo.save(branchOptional.get());
    }

    @Override
    public BranchResponse findById(Integer branchId) throws ResourceNotFoundException {
        Optional<Branch> branchOptional = branchRepo.findBranchByIdAndActiveFlag(branchId, 1);
        if (!branchOptional.isPresent()) {
            throw new ResourceNotFoundException("Branch with id " + branchId + " not found");
        }
        Branch branch = branchOptional.get();
        return BranchResponse.builder()
                .name(branch.getName())
                .code(branch.getCode())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .createDate(branch.getCreateDate())
                .updateDate(branch.getUpdateDate())
                .build();
    }

    @Override
    public BranchResponse findByCode(String code) throws ResourceNotFoundException {
        Optional<Branch> branchOptional = branchRepo.findBranchByCodeAndActiveFlag(code, 1);
        if (!branchOptional.isPresent()) {
            throw new ResourceNotFoundException("Branch with code " + code + " not found");
        }
        Branch branch = branchOptional.get();
        return BranchResponse.builder()
                .name(branch.getName())
                .code(branch.getCode())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .createDate(branch.getCreateDate())
                .updateDate(branch.getUpdateDate())
                .build();
    }
}
