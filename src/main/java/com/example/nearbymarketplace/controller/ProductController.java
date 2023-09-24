package com.example.nearbymarketplace.controller;

import com.example.nearbymarketplace.model.Product;
import com.example.nearbymarketplace.response.ResponseMessage;
import com.example.nearbymarketplace.service.CategoryService;
import com.example.nearbymarketplace.service.ProductService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all/{page}/{size}")
    public Page<Product> getAllProducts(@NotNull @PathVariable int page,
                                        @NotNull @PathVariable int size){

        return productService.getAllProducts(page, size);
    }

    @GetMapping("/byCategoryId/{categoryId}/{page}/{size}")
    public Page<Product> getAllProductsByCategory(@NotNull @PathVariable Long categoryId,
                                                  @NotNull @PathVariable int page,
                                                  @NotNull @PathVariable int size){

        categoryService.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        return productService.getAllProductsByCategory(categoryId, page, size);
    }

    //for filtering - products on sale
    @GetMapping("/allOnSale/{page}/{size}")
    public Page<Product> getAllProductsOnSale(@NotNull @PathVariable int page,
                                              @NotNull @PathVariable int size){
        return productService.findAllProductsOnSale(page, size);
    }

    //for filtering - when category and for sale are checked
    @GetMapping("/allOnSaleByCategory/{categoryId}/{page}/{size}")
    public Page<Product> getAllProductsOnSaleByCategory(@NotNull @PathVariable Long categoryId,
                                                        @NotNull @PathVariable int page,
                                                        @NotNull @PathVariable int size){

        categoryService.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        return productService.findProductsByCateforyIdOnSale(categoryId, page, size);
    }

    //for filtering - all products from cheapest
    @GetMapping("/allFromCheapest/{page}/{size}")
    public Page<Product> findAllFromCheapest(@NotNull @PathVariable int page,
                                             @NotNull @PathVariable int size){
        return productService.findFromCheapest(page, size);
    }

    //for filtering - all products from one category from cheapest
    @GetMapping("/FromCheapestbyCategoryId/{categoryId}/{page}/{size}")
    public Page<Product> getAllProductsFromCheapestByCategory(@NotNull @PathVariable Long categoryId,
                                                              @NotNull @PathVariable int page,
                                                              @NotNull @PathVariable int size){

        categoryService.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        return productService.getAllProductsFromCheapestByCategory(categoryId, page, size);
    }

    //get one product by id and increase views
    @PutMapping("/getProduct/{id}")
    public ResponseEntity<Product> getProduct(@NotNull @PathVariable Long id){
        Product p = productService.findByIdAndIncreaseViews(id);
        if(p != null){
            return ResponseEntity.ok(p);
        }
        return ResponseEntity.notFound().build();
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseMessage addProduct(@RequestParam(value = "name") String name,
                                      @RequestParam(value = "description", required = false) String description,
                                      @RequestParam(value = "price") Double price,
                                      @RequestParam(value = "image") MultipartFile multipartFile,
                                      @RequestParam(value = "latitude", required = false) Double latitude,
                                      @RequestParam(value = "longitude", required = false) Double longitude,
                                      @RequestParam(value = "category_id") Long categoryId){

        //to avoid exceptions
        if(latitude == null){
            latitude = 0.0;
        }
        if(longitude == null){
            longitude = 0.0;
        }

        return productService.addProduct(name, description, price, multipartFile, latitude, longitude, categoryId);
    }

    @RolesAllowed("ROLE_ADMIN")
    @PutMapping("/update/{productId}")
    public ResponseMessage updateProduct(@RequestParam(value = "name") String name,
                                         @RequestParam(value = "description") String description,
                                         @RequestParam(value = "price") Double price,
                                         @RequestParam(value = "image", required = false) MultipartFile multipartFile,
                                         @RequestParam(value = "latitude") Double latitude,
                                         @RequestParam(value = "longitude") Double longitude,
                                         @RequestParam(value = "category_id") Long categoryId,
                                         @RequestParam(value = "is_for_sale") Boolean isForSale,
                                         @NotNull @PathVariable Long productId) {

        return productService.updateProduct(name, description, price, multipartFile, latitude, longitude, categoryId, isForSale, productId);
    }

    @RolesAllowed("ROLE_ADMIN")
    @DeleteMapping("/delete/{productId}")
    public ResponseMessage deleteProduct(@NotNull @PathVariable Long productId){
        return productService.deleteproduct(productId);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<?> getImageById(@NotNull @PathVariable("id") Long id){
        byte[] image = productService.getImageById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @GetMapping("/nearest/{latitude}/{longitude}/{page}/{size}")
    public Page<Product> getNearestProduct(@NotNull @PathVariable("latitude") Double latitude,
                                           @NotNull @PathVariable("longitude") Double longitude, @NotNull @PathVariable int page, @NotNull @PathVariable int size,
                                     @RequestParam(value = "categoryId", required = false) Long categoryId){

        if(categoryId != null){
            return productService.getNearestProductByCategory(latitude, longitude, page, size, categoryId);
        } else {
            return productService.getNearestProduct(latitude, longitude, page, size);
        }
    }

}
