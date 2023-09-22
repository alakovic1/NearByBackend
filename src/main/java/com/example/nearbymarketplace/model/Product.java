package com.example.nearbymarketplace.model;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name="product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name can't be blank!")
    private String name;

    private String description;

    @NotNull
    private Double price;

    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(contentUsing = GeometryDeserializer.class)
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point coordinates;

    private Long views;

    private String image;

    //because not every price change to lower means it's on sale
    //changes only when it's edited
    @NotNull
    private Boolean isForSale;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="category_id")
    private Category category;

    public Product() {
    }

    public Product(Long id, String name, String description, Double price, Point coordinates, Long views, String image, Boolean isForSale, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.coordinates = coordinates;
        this.views = views;
        this.image = image;
        this.isForSale = isForSale;
        this.category = category;
    }

    public Product(Long id, String name, String description, Double price, Long views, String image, Boolean isForSale, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.views = views;
        this.image = image;
        this.isForSale = isForSale;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getForSale() {
        return isForSale;
    }

    public void setForSale(Boolean forSale) {
        isForSale = forSale;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }
}
