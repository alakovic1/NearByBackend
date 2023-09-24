package com.example.nearbymarketplace.service;

import com.example.nearbymarketplace.model.Product;
import com.example.nearbymarketplace.response.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Page<Product> findAllProductsOnSale(int page, int size);

    Page<Product> findProductsByCateforyIdOnSale(Long categoryId, int page, int size);

    Page<Product> getAllProducts(int page, int size);

    Page<Product> getAllProductsByCategory(Long categoryId, int page, int size);

    Optional<Product> findById(Long id);

    Page<Product> findFromCheapest(int page, int size);
    Page<Product> getAllProductsFromCheapestByCategory(Long categoryId, int page, int size);

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
