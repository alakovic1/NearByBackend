package com.example.nearbymarketplace.controller;

import com.example.nearbymarketplace.model.Category;
import com.example.nearbymarketplace.model.Product;
import com.example.nearbymarketplace.response.ResponseMessage;
import com.example.nearbymarketplace.service.CategoryService;
import com.example.nearbymarketplace.service.ProductService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/byCategoryId/{categoryId}")
    public List<Product> getAllProductsByCategory(@NotNull @PathVariable Long categoryId){

        categoryService.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        return productService.getAllProductsByCategory(categoryId);
    }

    //for filtering - products on sale
    @GetMapping("/allOnSale")
    public List<Product> getAllProductsOnSale(){
        return productService.findAllProductsOnSale();
    }

    //for filtering - when category and for sale are checked
    @GetMapping("/allOnSaleByCategory/{categoryId}")
    public List<Product> getAllProductsOnSaleByCategory(@NotNull @PathVariable Long categoryId){

        categoryService.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        return productService.findProductsByCateforyIdOnSale(categoryId);
    }

    //get one product by id and increase views
    @PutMapping("/getProduct/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id){
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
                                      @RequestParam(value = "image", required = false) MultipartFile multipartFile,
                                      @RequestParam(value = "latitude", required = false) Double latitude,
                                      @RequestParam(value = "longitude", required = false) Double longitude,
                                      @RequestParam(value = "category_id") Long categoryId){

        //todo radi - filesistem String s = productService.findPhotoAbsolutePath(multipartFile);

        return productService.addProduct(name, description, price, multipartFile, latitude, longitude, categoryId);
    }

    @RolesAllowed("ROLE_ADMIN")
    @PutMapping("/update/{productId}")
    public ResponseMessage updateProduct(@RequestParam(value = "name") String name,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam(value = "price") Double price,
                                                 @RequestParam(value = "image", required = false) MultipartFile multipartFile,
                                                 @RequestParam(value = "latitude", required = false) Double latitude,
                                                 @RequestParam(value = "longitude", required = false) Double longitude,
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

    /*@GetMapping("/image/filename/{filename}")
    public ResponseEntity<?> getImageByFilename(@PathVariable("filename") String filename){
        byte[] image = productService.getImageByFilename(filename);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }*/

    @GetMapping("/image/{id}")
    public ResponseEntity<?> getImageByFilename(@PathVariable("id") Long id){
        byte[] image = productService.getImageById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @GetMapping("/nearest/{latitude}/{longitude}")
    public Product getNearestProduct(@PathVariable("latitude") Double latitude,
                                     @PathVariable("longitude") Double longitude){

        return productService.getNearestProduct(latitude, longitude).get(0);

    }

}
