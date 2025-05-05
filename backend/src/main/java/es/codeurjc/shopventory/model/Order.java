package es.codeurjc.shopventory.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "OrderTable")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String userId;
    
    private String productId;
    
    private int quantity;
    
    private int totalPrice; // Assuming price is calculated based on productId and quantity, plus taxes and shipping
    
    private String status;
    
    private Date orderDate;
    
    private Date deliveryDate;

    public Order() {
    }

    public Order(String userId, String productId, int quantity, int totalPrice, String status, Date orderDate,
            Date deliveryDate) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }


}
