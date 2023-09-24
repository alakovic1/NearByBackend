package com.example.nearbymarketplace.service.impl;

import com.example.nearbymarketplace.model.Category;
import com.example.nearbymarketplace.model.Image;
import com.example.nearbymarketplace.model.PriceHistory;
import com.example.nearbymarketplace.model.Product;
import com.example.nearbymarketplace.repository.ImageRepository;
import com.example.nearbymarketplace.repository.PriceHistoryRepository;
import com.example.nearbymarketplace.repository.ProductRepository;
import com.example.nearbymarketplace.response.ResponseMessage;
import com.example.nearbymarketplace.service.CategoryService;
import com.example.nearbymarketplace.service.ProductService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    @Autowired
    private CategoryService categoryService;

    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public Page<Product> findAllProductsOnSale(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllByIsForSale(true, pageable);
    }

    public Page<Product> findProductsByCateforyIdOnSale(Long categoryId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategory_IdAndIsForSale(categoryId, true, pageable);
    }

    public Page<Product> getAllProducts(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public Page<Product> getAllProductsByCategory(Long categoryId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategory_Id(categoryId, pageable);
    }

    public Optional<Product> findById(Long id){
        return productRepository.findById(id);
    }

    public Page<Product> findFromCheapest(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("price"));
        return productRepository.findAll(pageable);
    }

    public Page<Product> getAllProductsFromCheapestByCategory(Long categoryId, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("price"));
        return productRepository.findByCategory_Id(categoryId, pageable);
    }

    public Product findByIdAndIncreaseViews(Long id){
        //update db for views
        Product p = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

        Long currentProductViews = p.getViews();
        p.setViews(currentProductViews + 1);
        return productRepository.save(p);
    }

    public Long uploadImage(MultipartFile multipartFile) {
        try {
            Image i = imageRepository.save(Image.builder()
                    .filename(multipartFile.getOriginalFilename())
                    .mimeType(multipartFile.getContentType())
                    .data(multipartFile.getBytes()).build());
            return i.getId();
        } catch (Exception e){
            System.out.println("error");
        }
        return null;
    }

    public byte[] getImageById(Long id){
        Image dbImage = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found!"));

        return dbImage.getData();
    }

    public ResponseMessage addProduct(String name,
                                      String description,
                                      Double price,
                                      MultipartFile multipartFile,
                                      Double x,
                                      Double y,
                                      Long categoryId) {

        Category c = categoryService.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        Product newProduct = new Product();
        newProduct.setName(name);
        newProduct.setDescription(description);
        newProduct.setPrice(price);
        //image will be shown with http://localhost:8080/product/image/{id}
        newProduct.setImage(String.valueOf(uploadImage(multipartFile)));
        newProduct.setCategory(c);
        newProduct.setForSale(false);
        newProduct.setViews(0L);
        newProduct.setCoordinates(geometryFactory.createPoint(new Coordinate(x,y)));

        Product p = productRepository.save(newProduct);

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setProduct(p);
        priceHistory.setPrice(price);
        priceHistory.setTimestamp(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
        priceHistoryRepository.save(priceHistory);

        return new ResponseMessage(true, HttpStatus.OK, "Product added successfully!");
    }

    public ResponseMessage updateProduct(String name,
                                  String description,
                                  Double price,
                                  MultipartFile multipartFile,
                                  Double x, Double y,
                                  Long categoryId, Boolean isForSale,
                                  Long productId) {

        Product p = findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

        Category c = categoryService.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        p.setName(name);
        p.setDescription(description);

        //if price changed - add value to PriceHistory
        if(!Objects.equals(price, p.getPrice())){
            PriceHistory priceHistory = new PriceHistory();
            priceHistory.setProduct(p);
            priceHistory.setPrice(price);
            priceHistory.setTimestamp(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
            priceHistoryRepository.save(priceHistory);
        }

        p.setPrice(price);
        p.setCategory(c);
        p.setForSale(isForSale);
        p.setCoordinates(geometryFactory.createPoint(new Coordinate(x,y)));

        //add image to db if doesn't exist, and delete old
        Long imageId = Long.valueOf(p.getImage());
        if(imageRepository.existsById(imageId) && multipartFile != null) {
            imageRepository.deleteById(imageId);
            imageId = uploadImage(multipartFile);
        }
        p.setImage(String.valueOf(imageId));
        productRepository.save(p);

        return new ResponseMessage(true, HttpStatus.OK, "Product successfully updated!");

    }

    public ResponseMessage deleteproduct(Long id){
        try {
            Product p = findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

            //first delete product from priceHistory - because of FK
            //if there is no price history of that product - still should delete product
            if(priceHistoryRepository.existsByProduct(p)) {
                List<PriceHistory> priceHistory = priceHistoryRepository.findAllByProduct(p);
                for(PriceHistory ph : priceHistory) {
                    priceHistoryRepository.deleteById(ph.getId());
                }
            }
            productRepository.deleteById(id);

            Long imageId = Long.parseLong(p.getImage());

            //delete image
            if(imageRepository.existsById(imageId)){
                imageRepository.deleteById(imageId);
            }

            return new ResponseMessage(true, HttpStatus.OK,
                    "Product successfully deleted!");

        } catch (RuntimeException e){
            return new ResponseMessage(false, HttpStatus.BAD_REQUEST,
                    "An exception with message: " + e.getMessage() +
                            ", was thrown. Product hasn't been deleted!");
        }
    }

    public Page<Product> getNearestProduct(Double x, Double y, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Point point = geometryFactory.createPoint(new Coordinate(x,y));
        return productRepository.findNearest(point, pageable);
    }

    public Page<Product> getNearestProductByCategory(Double x, Double y, int page, int size, Long categoryId){
        Pageable pageable = PageRequest.of(page, size);
        Point point = geometryFactory.createPoint(new Coordinate(x,y));
        return productRepository.findNearestByCategory(point, categoryId, pageable);
    }

}
