package com.example.nearbymarketplace.repository;

import com.example.nearbymarketplace.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByFilename(String filename);
}
