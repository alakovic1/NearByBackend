package com.example.nearbymarketplace.repository;

import com.example.nearbymarketplace.model.Product;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByIsForSale(Boolean sale, Pageable pageable);
    Page<Product> findByCategory_IdAndIsForSale(Long categoryId, Boolean sale, Pageable pageable);
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
    @Query(value="SELECT * FROM product ORDER BY ST_Distance(coordinates, :p)", nativeQuery = true)
    List<Product> findNearest(Point p);
}
