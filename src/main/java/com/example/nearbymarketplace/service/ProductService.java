package com.example.nearbymarketplace.service;

import com.example.nearbymarketplace.model.Product;
import com.example.nearbymarketplace.response.ResponseMessage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAllProductsOnSale();

    List<Product> findProductsByCateforyIdOnSale(Long categoryId);

    List<Product> getAllProducts();

    List<Product> getAllProductsByCategory(Long categoryId);

    Optional<Product> findById(Long id);

    List<Product> findFromCheapest();

    Product findByIdAndIncreaseViews(Long id);

    //String findPhotoAbsolutePath(MultipartFile multipartFile);

    Long uploadImage(MultipartFile multipartFile);

    //byte[] getImageByFilename(String filename);

    byte[] getImageById(Long id);

    ResponseMessage addProduct(String name,
                               String description,
                               Double price,
                               MultipartFile multipartFile,
                               Double x,
                               Double y,
                               Long categoryId);
    List<Product> getNearestProduct(Double x, Double y);

    ResponseMessage deleteproduct(Long id);

    ResponseMessage updateProduct(String name,
                                  String description,
                                  Double price,
                                  MultipartFile multipartFile,
                                  Double x,
                                  Double y,
                                  Long categoryId,
                                  Boolean isForSale,
                                  Long productId);
}
