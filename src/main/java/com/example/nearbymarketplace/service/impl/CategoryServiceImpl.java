package com.example.nearbymarketplace.service.impl;

import com.example.nearbymarketplace.model.Category;
import com.example.nearbymarketplace.repository.CategoryRepository;
import com.example.nearbymarketplace.response.ResponseMessage;
import com.example.nearbymarketplace.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public ResponseMessage addCategory(Category category){
        categoryRepository.save(category);
        return new ResponseMessage(true, HttpStatus.OK,
                "Category added successfully!");
    }

    public Optional<Category> findById(Long id){
        return categoryRepository.findById(id);
    }

    public ResponseMessage updateCategory(Category category, Long categoryId){
        try {

            Category oldCategory = categoryRepository
                    .findAll()
                    .stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .toList().get(0);
            oldCategory.setName(category.getName());
            categoryRepository.save(oldCategory);
            return new ResponseMessage(true, HttpStatus.OK,
                    "Category successfully updated!");

        } catch (RuntimeException e){
            return new ResponseMessage(false, HttpStatus.BAD_REQUEST,
                    "An exception with message: " + e.getMessage() +
                            ", was thrown. Category hasn't been updated!");
        }
    }

    public ResponseMessage deleteCategory(Long categoryId){
        try {

            categoryRepository.deleteById(categoryId);
            return new ResponseMessage(true, HttpStatus.OK,
                    "Category successfully deleted!");

        } catch (RuntimeException e){
            return new ResponseMessage(false, HttpStatus.BAD_REQUEST,
                    "An exception with message: " + e.getMessage() +
                    ", was thrown. Category hasn't been deleted!");
        }
    }

}
