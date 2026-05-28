package es.codeurjc.shopventory.model;

import java.math.BigDecimal;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity(name = "ProductTable")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @Column(unique = true)
    private String sku;

    private String description;

    private String descriptionShort;

    @Lob
    @JsonIgnore
    private Blob productImage;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price = BigDecimal.ZERO;

    @Min(0)
    private int stock = 0;

    @Min(0)
    private int minStockThreshold = 5;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "product_provider",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "provider_id")
    )
    private Set<Provider> providers = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    public Product() {}

    public Product(String name, String sku, String description, String descriptionShort,
                   BigDecimal price, int stock, int minStockThreshold, Set<String> categories) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.descriptionShort = descriptionShort;
        this.price = price;
        this.stock = stock;
        this.minStockThreshold = minStockThreshold;
        this.categories = categories;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isLowStock() {
        return stock <= minStockThreshold;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDescriptionShort() { return descriptionShort; }
    public void setDescriptionShort(String descriptionShort) { this.descriptionShort = descriptionShort; }

    public Blob getProductImage() { return productImage; }
    public void setProductImage(Blob productImage) { this.productImage = productImage; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getMinStockThreshold() { return minStockThreshold; }
    public void setMinStockThreshold(int minStockThreshold) { this.minStockThreshold = minStockThreshold; }

    public Set<String> getCategories() { return categories; }
    public void setCategories(Set<String> categories) { this.categories = categories; }

    public Set<Provider> getProviders() { return providers; }
    public void setProviders(Set<Provider> providers) { this.providers = providers; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
