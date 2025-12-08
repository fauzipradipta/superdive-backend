package com.example.superdive.backend.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderitem_id")
    private Long id; 

    @ManyToOne(fetch = FetchType.LAZY) // Added FetchType.LAZY
    @JoinColumn(name = "Order_id", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private Order Order;

    @ManyToOne(fetch = FetchType.LAZY) // Added FetchType.LAZY
    @JoinColumn(name = "product_id", nullable = false) // Added nullable = false
    private Product product;
    
    private Integer qty; // Changed from int to Integer to avoid primitive default values
    
    private BigDecimal price; 
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Order getOrder() {
        return Order;
    }
    public void setOrder(Order Order) {
        this.Order = Order;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Integer getQty() {
        return qty;
    }
    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public OrderItem(Long id, Order Order, Product product, Integer qty, BigDecimal price) {
        this.id = id;
        this.Order = Order;
        this.product = product;
        this.qty = qty;
        this.price = price;
    }

    public OrderItem() {
        // Default constructor
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", Order=" + (Order != null ? Order.getId() : "null") +
                ", product=" + (product != null ? product.getId() : "null") +
                ", qty=" + qty +
                ", price=" + price +
                '}';
    }
}