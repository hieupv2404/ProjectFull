package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.IssueDetailRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.IssueDetailResponse;
import com.nuce.group3.data.model.*;
import com.nuce.group3.data.repo.*;
import com.nuce.group3.enums.EnumStatus;
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

    @Autowired
    private ProductDetailRepo productDetailRepo;

    @Autowired
    private ShelfRepo shelfRepo;

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
    public GenericResponse findIssueDetailByFilter(BigDecimal priceTotalFrom, BigDecimal priceTotalTo, String issueCode, String productInfo, Integer page, Integer size) {
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
        return new GenericResponse(issueDetailResponses.size(), issueDetailResponses);
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
        Optional<IssueDetail> issueDetailOptional = issueDetailRepo.findIssueDetailByIssueAndProductAndImei(issueDetailRequest.getIssueId(), issueDetailRequest.getProductId(), issueDetailRequest.getImei());
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

        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByImeiAndStatusAndActiveFlag(issueDetailRequest.getImei(), EnumStatus.VALID.name(), 1);
        if (!productDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Detail with imei: " + issueDetailRequest.getImei() + " not found");
        }

        IssueDetail issueDetail = new IssueDetail();
        issueDetail.setIssue(issueOptional.get());
        issueDetail.setProductInfo(productInfoOptional.get());
        issueDetail.setImei(issueDetailRequest.getImei());
        issueDetail.setPriceOne(productInfoOptional.get().getPriceOut());
        issueDetail.setActiveFlag(1);
        issueDetailRepo.save(issueDetail);

        productInfoOptional.get().setQty(productInfoOptional.get().getQty() - 1);
        productInfoRepo.save(productInfoOptional.get());

        issueOptional.get().setPrice(issueOptional.get().getPrice().add(issueDetail.getPriceOne()));
        issueRepo.save(issueOptional.get());

        productDetailOptional.get().setStatus(EnumStatus.INVALID);
        productDetailRepo.save(productDetailOptional.get());

        Optional<Shelf> shelfOptional = shelfRepo.findShelfByIdAndActiveFlag(productDetailOptional.get().getShelf().getId(), 1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Shelf with ID: " + productDetailOptional.get().getShelf().getId() + " not found");
        }

        shelfOptional.get().setQty(shelfOptional.get().getQty() - 1);
        shelfRepo.save(shelfOptional.get());
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
            issue.setPrice(issue.getPrice().subtract(issueDetailOptional.get().getPriceOne()));
            issueRepo.save(issue);
        }
        Optional<ProductDetail> productDetailOptional = productDetailRepo.findProductDetailByImeiAndStatusAndActiveFlag(issueDetailOptional.get().getImei(), EnumStatus.INVALID.name(), 1);
        if (!productDetailOptional.isPresent()) {
            throw new ResourceNotFoundException("Product Detail  with imei: " + issueDetailOptional.get().getImei() + "not found");
        }
        productDetailOptional.get().setStatus(EnumStatus.VALID);
        productDetailRepo.save(productDetailOptional.get());

        issueDetailOptional.get().getProductInfo().setQty(issueDetailOptional.get().getProductInfo().getQty() + 1);
        productInfoRepo.save(issueDetailOptional.get().getProductInfo());

        Optional<Shelf> shelfOptional = shelfRepo.findShelfByIdAndActiveFlag(productDetailOptional.get().getShelf().getId(), 1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Shelf  with ID: " + productDetailOptional.get().getShelf().getId() + "not found");
        }
        shelfOptional.get().setQty(shelfOptional.get().getQty() + 1);
        shelfRepo.save(shelfOptional.get());
    }
}
