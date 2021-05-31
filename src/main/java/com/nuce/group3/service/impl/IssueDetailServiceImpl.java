package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.IssueDetailRequest;
import com.nuce.group3.controller.dto.response.IssueDetailResponse;
import com.nuce.group3.data.model.Issue;
import com.nuce.group3.data.model.IssueDetail;
import com.nuce.group3.data.model.ProductInfo;
import com.nuce.group3.data.repo.IssueDetailRepo;
import com.nuce.group3.data.repo.IssueRepo;
import com.nuce.group3.data.repo.ProductInfoRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.IssueDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class IssueDetailServiceImpl implements IssueDetailService {

    @Autowired
    private IssueRepo issueRepo;

    @Autowired
    private ProductInfoRepo productInfoRepo;

    @Autowired
    private IssueDetailRepo issueDetailRepo;

    @Override
    public List<IssueDetailResponse> getAll(Integer page, Integer size) {
        List<IssueDetailResponse> issueDetailResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        issueDetailRepo.findIssueDetailByActiveFlag(1, PageRequest.of(page, size)).forEach(issueDetail -> {
            IssueDetailResponse issueDetailResponse = IssueDetailResponse.builder()
                    .productName(issueDetail.getProductInfo().getName())
                    .imei(issueDetail.getImei())
                    .price(issueDetail.getPriceOne())
                    .issueCode(issueDetail.getIssue().getCode())
                    .build();
            issueDetailResponses.add(issueDetailResponse);
        });
        return issueDetailResponses;
    }

    @Override
    public List<IssueDetailResponse> findIssueDetailByFilter(BigDecimal priceTotalFrom, BigDecimal priceTotalTo, String issueCode, String productInfo, Integer page, Integer size) {
        List<IssueDetailResponse> issueDetailResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        issueDetailRepo.findIssueDetailByFilter(priceTotalFrom, priceTotalTo, issueCode, productInfo, PageRequest.of(page, size)).forEach(issueDetail -> {
            IssueDetailResponse issueDetailResponse = IssueDetailResponse.builder()
                    .productName(issueDetail.getProductInfo().getName())
                    .imei(issueDetail.getImei())
                    .price(issueDetail.getPriceOne())
                    .issueCode(issueDetail.getIssue().getCode())
                    .build();
            issueDetailResponses.add(issueDetailResponse);
        });
        return issueDetailResponses;
    }

    @Override
    public IssueDetailResponse findIssueDetailById(Integer issueDetailId) throws ResourceNotFoundException {
        if (issueDetailId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<IssueDetail> issueDetailOptional = issueDetailRepo.findIssueDetailByIdAndActiveFlag(issueDetailId, 1);
        if (!issueDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Issue Detail with " + issueDetailId + " not found!");
        }
        IssueDetail issueDetail = issueDetailOptional.get();
        return IssueDetailResponse.builder()
                .productName(issueDetail.getProductInfo().getName())
                .imei(issueDetail.getImei())
                .price(issueDetail.getPriceOne())
                .issueCode(issueDetail.getIssue().getCode())
                .build();
    }

    @Override
    public void save(IssueDetailRequest issueDetailRequest) throws LogicException, ResourceNotFoundException {
        Optional<IssueDetail> issueDetailOptional = issueDetailRepo.findIssueDetailByIssueAndProduct(issueDetailRequest.getIssueId(), issueDetailRequest.getProductId());
        if (issueDetailOptional.isPresent()) {
            throw new LogicException("Issue Detail Existed", HttpStatus.BAD_REQUEST);
        }
        Optional<Issue> issueOptional = issueRepo.findIssueByIdAndActiveFlag(issueDetailRequest.getIssueId(), 1);
        if (!issueOptional.isPresent()) {
            throw new ResourceNotFoundException("Issue with id " + issueDetailRequest.getIssueId() + " not found");
        }

        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(issueDetailRequest.getProductId(), 1);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Info with id: " + issueDetailRequest.getProductId() + " not found");
        }

        IssueDetail issueDetail = new IssueDetail();
        issueDetail.setIssue(issueOptional.get());
        issueDetail.setProductInfo(productInfoOptional.get());
        issueDetail.setQty(issueDetailRequest.getQty());
        issueDetail.setPriceOne(issueDetailRequest.getPriceOne());
        issueDetail.setActiveFlag(1);
        issueDetailRepo.save(issueDetail);

        issueOptional.get().setPrice(issueOptional.get().getPrice().add(issueDetail.getPriceOne().multiply(BigDecimal.valueOf(issueDetail.getQty()))));
        issueRepo.save(issueOptional.get());
    }

    @Override
    public IssueDetailResponse edit(Integer issueDetailId, IssueDetailRequest issueDetailRequest) throws ResourceNotFoundException, LogicException {
        Optional<IssueDetail> issueDetailOptional = issueDetailRepo.findIssueDetailByIdAndActiveFlag(issueDetailId, 1);
        if (!issueDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("IssueDetail with ID: " + issueDetailId + " not found!");
        }
        IssueDetail issueDetail = issueDetailOptional.get();
        Optional<Issue> issueOptional = issueRepo.findIssueByIdAndActiveFlag(issueDetailRequest.getIssueId(), 1);
        if (!issueOptional.isPresent()) {
            throw new ResourceNotFoundException("Issue with id " + issueDetailRequest.getIssueId() + " not found");
        }

        Optional<ProductInfo> productInfoOptional = productInfoRepo.findProductInfoByIdAndActiveFlag(issueDetailRequest.getProductId(), 1);
        if (!productInfoOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Info with id: " + issueDetailRequest.getProductId() + " not found");
        }

        Optional<IssueDetail> issueDetailOptional2 = issueDetailRepo.findIssueDetailByIssueAndProduct(issueDetailRequest.getIssueId(), issueDetailRequest.getProductId());
        if (issueDetailOptional2.isPresent()) {
            throw new LogicException("Issue Detail Existed", HttpStatus.BAD_REQUEST);
        }

        BigDecimal oldPriceFromIssueDetail = issueDetail.getPriceOne().multiply(BigDecimal.valueOf(issueDetail.getQty()));

        issueDetail.setIssue(issueOptional.get());
        issueDetail.setProductInfo(productInfoOptional.get());
        issueDetail.setQty(issueDetailRequest.getQty());
        issueDetail.setPriceOne(issueDetailRequest.getPriceOne());
        try {
            issueDetailRepo.save(issueDetail);
            issueOptional.get().setPrice(issueOptional.get().getPrice().subtract(oldPriceFromIssueDetail).add(issueDetail.getPriceOne().multiply(BigDecimal.valueOf(issueDetail.getQty()))));
            issueRepo.save(issueOptional.get());
            return IssueDetailResponse.builder()
                    .priceTotal(issueDetail.getPriceOne().multiply(BigDecimal.valueOf(issueDetail.getQty())))
                    .priceOne(issueDetail.getPriceOne())
                    .productInfo(issueDetail.getProductInfo().getName())
                    .qty(issueDetail.getQty())
                    .issueCode(issueDetail.getIssue().getCode())
                    .build();
        } catch (Exception e) {
            throw new LogicException("Edit error", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Integer issueDetailId, boolean isDeletedParent) throws ResourceNotFoundException {
        Optional<IssueDetail> issueDetailOptional = issueDetailRepo.findIssueDetailByIdAndActiveFlag(issueDetailId, 1);
        if (!issueDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("IssueDetail with " + issueDetailId + " not found!");
        }

        issueDetailOptional.get().setActiveFlag(0);
        issueDetailRepo.save(issueDetailOptional.get());
        if (!isDeletedParent) {
            Issue issue = issueDetailOptional.get().getIssue();
            issue.setPrice(issue.getPrice().subtract(issueDetailOptional.get().getPriceOne().multiply(BigDecimal.valueOf(issueDetailOptional.get().getQty()))));
            issueRepo.save(issue);
        }


    }
}
