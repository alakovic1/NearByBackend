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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public List<Product> findAllProductsOnSale(){
        return productRepository.findAllByIsForSale(true);
    }

    public List<Product> findProductsByCateforyIdOnSale(Long categoryId){
        return productRepository.findByCategory_IdAndIsForSale(categoryId, true);
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public List<Product> getAllProductsByCategory(Long categoryId){
        return productRepository.findByCategory_Id(categoryId);
    }

    public Optional<Product> findById(Long id){
        return productRepository.findById(id);
    }

    public Product findByIdAndIncreaseViews(Long id){
        //update db for views
        Product p = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

        Long currentProductViews = p.getViews();
        p.setViews(currentProductViews + 1);
        return productRepository.save(p);
    }

    //for image - from filesystem
    /*public String findPhotoAbsolutePath(MultipartFile multipartFile){
        try {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            String uploadDir = "./photos/";

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                //return filePath.toFile().getAbsolutePath();
                return "http://localhost:8080/photos/" + fileName;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return "";
        } catch (Exception e){
            System.out.println("The photo couldn't be uploaded!");
        }

        return "";
    }*/

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

    /*public byte[] getImageByFilename(String filename){
        Optional<Image> dbImage = imageRepository.findByFilename(filename);
        byte[] image = dbImage.get().getData();
        return image;
    }*/

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
                PriceHistory priceHistory = priceHistoryRepository.findByProduct(p)
                        .orElseThrow(() -> new ResourceNotFoundException("ProductHistory not found!"));
                priceHistoryRepository.deleteById(priceHistory.getId());
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

    public List<Product> getNearestProduct(Double x, Double y){
        Point point = geometryFactory.createPoint(new Coordinate(x,y));
        return productRepository.findNearest(point);
    }

}
