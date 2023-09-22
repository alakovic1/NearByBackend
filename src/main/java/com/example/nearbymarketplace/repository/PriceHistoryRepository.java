package com.example.nearbymarketplace.repository;

import com.example.nearbymarketplace.model.PriceHistory;
import com.example.nearbymarketplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    Optional<PriceHistory> findByProduct(Product product);
    boolean existsByProduct(Product product);
}
