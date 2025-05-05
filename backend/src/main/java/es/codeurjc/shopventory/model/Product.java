package es.codeurjc.shopventory.model;

import java.sql.Blob;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity(name = "ProductTable")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    @Lob
    @JsonIgnore
    private Blob productImage;

    private Long price;

    private int stock;

    private String descriptionShort;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> category;


    public Product() {
    }

    public Product(String name, String description, Blob productImage, Long price, int stock, String descriptionShort,
            Set<String> category) {
        this.name = name;
        this.description = description;
        this.productImage = productImage;
        this.price = price;
        this.stock = stock;
        this.descriptionShort = descriptionShort;
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

    public Blob getProductImage() {
        return productImage;
    }

    public void setProductImage(Blob productImage) {
        this.productImage = productImage;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescriptionShort() {
        return descriptionShort;
    }

    public void setDescriptionShort(String descriptionShort) {
        this.descriptionShort = descriptionShort;
    }

    public Set<String> getCategory() {
        return category;
    }

    public void setCategory(Set<String> category) {
        this.category = category;
    }


}
