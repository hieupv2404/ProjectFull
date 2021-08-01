package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.CategoryRequest;
import com.nuce.group3.controller.dto.response.CategoryResponse;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.data.model.Category;
import com.nuce.group3.exception.LogicException;

import java.util.List;

public interface CategoryService {
    List<Category> getAll();

    GenericResponse findCategoryByFilter(String name, String code, Integer page, Integer size);

    void save(CategoryRequest categoryRequest) throws LogicException;

    CategoryResponse edit(Integer categoryId, CategoryRequest categoryRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer categoryId) throws ResourceNotFoundException, LogicException;

    Category findById(Integer categoryId) throws ResourceNotFoundException;
}
