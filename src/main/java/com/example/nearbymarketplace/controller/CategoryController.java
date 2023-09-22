package com.example.nearbymarketplace.controller;

import com.example.nearbymarketplace.model.Category;
import com.example.nearbymarketplace.response.ResponseMessage;
import com.example.nearbymarketplace.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public List<Category> getAllCategories(){
        return categoryService.getAllCategories();
    }

    @PostMapping("/add")
    public ResponseMessage addCategory(@Valid @RequestBody Category category){
        return categoryService.addCategory(category);
    }

    @PutMapping("/update/{categoryId}")
    public ResponseMessage updateCategory(@Valid @RequestBody Category category,
                                          @NotNull @PathVariable Long categoryId){
        categoryService.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        return categoryService.updateCategory(category, categoryId);
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseMessage deleteCategory(@NotNull @PathVariable Long categoryId){
        categoryService.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        return categoryService.deleteCategory(categoryId);
    }

}
