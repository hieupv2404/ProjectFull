package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.IssueDetailRequest;
import com.nuce.group3.controller.dto.response.IssueDetailResponse;
import com.nuce.group3.exception.LogicException;

import java.math.BigDecimal;
import java.util.List;

public interface IssueDetailService {
    List<IssueDetailResponse> getAll(Integer page, Integer size);

    List<IssueDetailResponse> findIssueDetailByFilter(BigDecimal priceTotalFrom, BigDecimal priceTotalTo, String issueCode, String productInfo, Integer page, Integer size);

    void save(IssueDetailRequest issueDetailRequest) throws LogicException, ResourceNotFoundException;

    IssueDetailResponse edit(Integer issueDetailId, IssueDetailRequest issueDetailRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer issueDetailId, boolean isDeletedParent) throws ResourceNotFoundException;

    IssueDetailResponse findIssueDetailById(Integer issueDetailId) throws ResourceNotFoundException;

}
