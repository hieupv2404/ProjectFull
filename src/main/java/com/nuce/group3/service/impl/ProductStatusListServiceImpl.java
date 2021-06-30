package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ProductStatusListRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.ProductStatusListResponse;
import com.nuce.group3.data.model.ProductStatusList;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.data.model.Vat;
import com.nuce.group3.data.repo.ProductStatusDetailRepo;
import com.nuce.group3.data.repo.ProductStatusListRepo;
import com.nuce.group3.data.repo.UserRepo;
import com.nuce.group3.data.repo.VatRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.ProductStatusDetailService;
import com.nuce.group3.service.ProductStatusListService;
import com.nuce.group3.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProductStatusListServiceImpl implements ProductStatusListService {

    @Autowired
    private VatRepo vatRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductStatusListRepo productStatusListRepo;

    @Autowired
    private ProductStatusDetailRepo productStatusDetailRepo;

    @Autowired
    private ProductStatusDetailService productStatusDetailService;

    @Override
    public List<ProductStatusListResponse> getAll(int type, Integer page, Integer size) {
        List<ProductStatusListResponse> productStatusListResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        productStatusListRepo.findProductStatusListByActiveFlag(type, 1, PageRequest.of(page, size)).forEach(productStatusList -> {
            ProductStatusListResponse productStatusListResponse = ProductStatusListResponse.builder()
                    .code(productStatusList.getCode())
                    .vatCode(productStatusList.getVat().getCode())
                    .userName(productStatusList.getUser().getName())
                    .price(productStatusList.getPrice())
                    .createDate(productStatusList.getCreateDate())
                    .updateDate(productStatusList.getUpdateDate())
                    .build();
            productStatusListResponses.add(productStatusListResponse);
        });
        return productStatusListResponses;
    }

    @Override
    public GenericResponse findProductStatusListByFilter(String code, String vatCode, BigDecimal priceFrom, BigDecimal priceTo, int type, Integer page, Integer size) {
        List<ProductStatusListResponse> productStatusListResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        productStatusListRepo.findProductStatusListByFilter(code, vatCode, priceFrom, priceTo, type, PageRequest.of(page, size)).forEach(productStatusList -> {
            ProductStatusListResponse productStatusListResponse = ProductStatusListResponse.builder()
                    .id(productStatusList.getId())
                    .code(productStatusList.getCode())
                    .vatCode(productStatusList.getVat().getCode())
                    .userName(productStatusList.getUser().getName())
                    .price(productStatusList.getPrice())
                    .createDate(productStatusList.getCreateDate())
                    .updateDate(productStatusList.getUpdateDate())
                    .build();
            productStatusListResponses.add(productStatusListResponse);
        });
        return new GenericResponse(productStatusListResponses.size(), productStatusListResponses);
    }

    @Override
    public ProductStatusListResponse findProductStatusListById(Integer productStatusListId) throws ResourceNotFoundException {
        if (productStatusListId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByIdAndActiveFlag(productStatusListId, 1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusList with " + productStatusListId + " not found!");
        }
        ProductStatusList productStatusList = productStatusListOptional.get();
        return ProductStatusListResponse.builder()
                .code(productStatusList.getCode())
                .vatCode(productStatusList.getVat().getCode())
                .userName(productStatusList.getUser().getName())
                .price(productStatusList.getPrice())
                .createDate(productStatusList.getCreateDate())
                .updateDate(productStatusList.getUpdateDate())
                .build();
    }

    @Override
    public void save(Integer vatId, ProductStatusListRequest productStatusListRequest) throws LogicException, ResourceNotFoundException {
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByVatAndType(vatId, 1);
        if (productStatusListOptional.isPresent()) {
            throw new LogicException("ProductStatusList Existed", HttpStatus.BAD_REQUEST);
        }
        Optional<Vat> vatOptional = vatRepo.findVatByIdAndActiveFlag(vatId, 1);
        if (!vatOptional.isPresent()) {
            throw new ResourceNotFoundException("Vat with id " + vatId + " not found");
        }

        Optional<Users> usersOptional = userRepo.findUsersByUserName(productStatusListRequest.getUserName());
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with user name: " + productStatusListRequest.getUserName() + " not found");
        }

        Calendar calendar = Calendar.getInstance();
        long maxId = productStatusListRepo.countProductStatusListByTypeAndActiveFlag(Constant.PRODUCT_DONE) + 1;
        ProductStatusList productStatusListDone = new ProductStatusList();
        productStatusListDone.setCode("DONE" + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.MONTH + 1) + calendar.get(Calendar.YEAR) + maxId);
        productStatusListDone.setPrice(new BigDecimal(0));
        productStatusListDone.setUser(usersOptional.get());
        productStatusListDone.setVat(vatOptional.get());
        productStatusListDone.setType(Constant.PRODUCT_DONE);
        productStatusListDone.setActiveFlag(1);
        productStatusListDone.setCreateDate(new Date());
        productStatusListDone.setUpdateDate(new Date());
        productStatusListRepo.save(productStatusListDone);

        String newCodeFromDone = productStatusListDone.getCode().replace("DONE", "BACK");

        ProductStatusList productStatusListBack = new ProductStatusList();
        productStatusListDone.setCode(newCodeFromDone);
        productStatusListDone.setPrice(new BigDecimal(0));
        productStatusListDone.setUser(usersOptional.get());
        productStatusListDone.setVat(vatOptional.get());
        productStatusListDone.setType(Constant.PRODUCT_BACK);
        productStatusListDone.setActiveFlag(1);
        productStatusListDone.setCreateDate(new Date());
        productStatusListDone.setUpdateDate(new Date());
        productStatusListRepo.save(productStatusListBack);
    }

    @Override
    public ProductStatusListResponse edit(Integer productStatusListId, ProductStatusListRequest productStatusListRequest, String userName) throws ResourceNotFoundException, LogicException {
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByIdAndActiveFlag(productStatusListId, 1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusList with " + productStatusListId + " not found!");
        }
        ProductStatusList productStatusList = productStatusListOptional.get();

        Optional<Users> usersOptional = userRepo.findUsersByUserName(userName);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with user name: " + userName + " not found");
        }

        productStatusList.setUser(usersOptional.get());
        productStatusList.setUpdateDate(new Date());
        try {
            productStatusListRepo.save(productStatusList);
            return ProductStatusListResponse.builder()
                    .code(productStatusList.getCode())
                    .vatCode(productStatusList.getVat().getCode())
                    .userName(productStatusList.getUser().getName())
                    .price(productStatusList.getPrice())
                    .createDate(productStatusList.getCreateDate())
                    .updateDate(productStatusList.getUpdateDate())
                    .build();
        } catch (Exception e) {
            throw new LogicException("Edit error", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Integer productStatusListId) throws ResourceNotFoundException {
        Optional<ProductStatusList> productStatusListOptional = productStatusListRepo.findProductStatusListByIdAndActiveFlag(productStatusListId, 1);
        if (!productStatusListOptional.isPresent()) {
            throw new ResourceNotFoundException("ProductStatusList with " + productStatusListId + " not found!");
        }
        productStatusListOptional.get().setActiveFlag(0);
        productStatusListRepo.save(productStatusListOptional.get());

        productStatusDetailRepo.findProductStatusDetailByProductStatusListId(productStatusListId).forEach(productStatusDetail -> {
            try {
                productStatusDetailService.delete(productStatusDetail.getId(), true);
            } catch (ResourceNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

}
