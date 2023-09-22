package com.example.nearbymarketplace.repository;

import com.example.nearbymarketplace.model.Product;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByIsForSale(Boolean sale);
    List<Product> findByCategory_IdAndIsForSale(Long categoryId, Boolean sale);
    List<Product> findByCategory_Id(Long categoryId);
    @Query(value="SELECT * FROM product ORDER BY ST_Distance(coordinates, :p)", nativeQuery = true)
    List<Product> findNearest(Point p);
}
