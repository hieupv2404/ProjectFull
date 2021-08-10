package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.CategoryRequest;
import com.nuce.group3.controller.dto.response.CategoryResponse;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.data.model.Category;
import com.nuce.group3.data.repo.CategoryRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.CategoryService;
import com.nuce.group3.service.ProductInfoService;
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
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ProductInfoService productInfoService;

    @Override
    public List<Category> getAll() {
        return categoryRepo.getAllCategoryByActiveFlag(1);
    }

    @Override
    public GenericResponse findCategoryByFilter(String name, String code, Integer page, Integer size) {
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        categoryRepo.findCategoryByFilter(name, code, PageRequest.of(page, size)).forEach(category -> {
            CategoryResponse categoryResponse = CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .code(category.getCode())
                    .createDate(category.getCreateDate())
                    .updateDate(category.getUpdateDate())
                    .build();
            categoryResponses.add(categoryResponse);
        });
        return new GenericResponse(categoryRepo.findCategoryByFilter(name, code, PageRequest.of(page, 1000)).size(), categoryResponses);
    }

    @Override
    public void save(CategoryRequest categoryRequest) throws LogicException {
        Optional<Category> categoryOptional = categoryRepo.findCategoryByActiveFlagAndCode(1, categoryRequest.getCode());
        if (categoryOptional.isPresent()) {
            throw new LogicException("Code is existed!", HttpStatus.BAD_REQUEST);
        }
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setCode(categoryRequest.getCode());
        category.setDescription(categoryRequest.getDescription());
        category.setActiveFlag(1);
        category.setCreateDate(new Date());
        category.setUpdateDate(new Date());
        categoryRepo.save(category);
    }

    @Override
    public CategoryResponse edit(Integer categoryId, CategoryRequest categoryRequest) throws ResourceNotFoundException, LogicException {
        Optional<Category> categoryOptional = categoryRepo.findCategoryByActiveFlagAndId(1, categoryId);
        if (!categoryOptional.isPresent())
            throw new ResourceNotFoundException("Category with id " + categoryId + " not found");
        Optional<Category> categoryFindByCodeOptional = categoryRepo.findCategoryByActiveFlagAndCode(1, categoryRequest.getCode());
        if (!categoryRequest.getCode().equals(categoryOptional.get().getCode()) && categoryFindByCodeOptional.isPresent())
            throw new LogicException("Category with code existed!", HttpStatus.BAD_REQUEST);
        categoryOptional.get().setName(categoryRequest.getName());
        categoryOptional.get().setCode(categoryRequest.getCode());
        categoryOptional.get().setDescription(categoryRequest.getDescription());
        categoryOptional.get().setActiveFlag(1);
        categoryOptional.get().setUpdateDate(new Date());
        Category category = categoryRepo.save(categoryOptional.get());
        return CategoryResponse.builder()
                .name(category.getName())
                .description(category.getDescription())
                .code(category.getCode())
                .createDate(category.getCreateDate())
                .updateDate(category.getUpdateDate())
                .build();
    }

    @Override
    public void delete(Integer categoryId) throws ResourceNotFoundException, LogicException {
        Optional<Category> categoryOptional = categoryRepo.findCategoryByActiveFlagAndId(1, categoryId);
        if (!categoryOptional.isPresent()) {
            throw new ResourceNotFoundException("Category with id " + categoryId + " not found");
        }
        if (!categoryOptional.get().getProductInfos().isEmpty()) {
            throw new LogicException("Remain Products in the Branch!", HttpStatus.BAD_REQUEST);
        }
        categoryOptional.get().setActiveFlag(0);
        categoryRepo.save(categoryOptional.get());
    }

    @Override
    public Category findById(Integer categoryId) throws ResourceNotFoundException {
        Optional<Category> categoryOptional = categoryRepo.findCategoryByActiveFlagAndId(1, categoryId);
        if (!categoryOptional.isPresent()) {
            throw new ResourceNotFoundException("Category with id " + categoryId + " not found");
        }
        return categoryOptional.get();
    }
}
