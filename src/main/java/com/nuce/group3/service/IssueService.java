package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.response.IssueResponse;
import com.nuce.group3.exception.LogicException;

import java.util.List;

public interface IssueService {
    List<IssueResponse> getAll(Integer page, Integer size);

    List<IssueResponse> findIssueByFilter(String code, String customerName, String userName, int branchId, Integer page, Integer size);

    IssueResponse findIssueById(Integer issueId) throws ResourceNotFoundException;

    void save(int customerId, String userName) throws LogicException, ResourceNotFoundException;

    void delete(Integer issueId) throws ResourceNotFoundException;
}
