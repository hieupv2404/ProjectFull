package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.CategoryRequest;
import com.nuce.group3.controller.dto.response.CategoryResponse;
import com.nuce.group3.data.model.Category;
import com.nuce.group3.data.repo.CategoryRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin(origins = "**")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @HasRole({"ADMIN","ADMIN_PTTK"})
    public ResponseEntity<List<Category>> findCategoryByFilter(@RequestParam(name = "name", required = false) String name,
                                                               @RequestParam(name="code", required = false) String code)
    {
        return new ResponseEntity<>(categoryService.findCategoryByFilter(name,code), HttpStatus.OK);
    }

    @PostMapping
    @HasRole({"ADMIN","ADMIN_PTTK"})
    public ResponseEntity<String> createCategory(@RequestBody @Valid CategoryRequest categoryRequest)
    {
        return new ResponseEntity<>("Created!", HttpStatus.OK);
    }

    @PutMapping("/edit/{id}")
    @HasRole({"ADMIN","ADMIN_PTTK"})
    public ResponseEntity<CategoryResponse> updateCategory(@RequestBody @Valid CategoryRequest categoryRequest,
                                                           @PathVariable Integer categoryId) throws LogicException, ResourceNotFoundException {
        if(categoryId==null)
            throw  new LogicException("Category is null", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(categoryService.edit(categoryId, categoryRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{id}")
    @HasRole({"ADMIN","ADMIN_PTTK"})
    public ResponseEntity<String> deleteCategory(@PathVariable Integer categoryId) throws ResourceNotFoundException, LogicException {
        if(categoryId==null)
            throw  new LogicException("Category is null", HttpStatus.BAD_REQUEST);
        categoryService.delete(categoryId);
        return new ResponseEntity<>("Deleted category id "+categoryId, HttpStatus.OK);
    }
}
