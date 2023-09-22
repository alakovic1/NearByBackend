package com.example.nearbymarketplace.service;

import com.example.nearbymarketplace.model.Category;
import com.example.nearbymarketplace.response.ResponseMessage;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();

    ResponseMessage addCategory(Category category);

    Optional<Category> findById(Long id);

    ResponseMessage updateCategory(Category category, Long categoryId);

    ResponseMessage deleteCategory(Long categoryId);
}
