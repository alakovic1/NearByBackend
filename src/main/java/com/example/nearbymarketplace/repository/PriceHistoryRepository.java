package com.example.nearbymarketplace.repository;

import com.example.nearbymarketplace.model.PriceHistory;
import com.example.nearbymarketplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    boolean existsByProduct(Product product);

    List<PriceHistory> findAllByProduct(Product product);
}
