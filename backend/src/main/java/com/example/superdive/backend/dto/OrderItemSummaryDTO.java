package com.example.superdive.backend.dto;

import java.math.BigDecimal;

import com.example.superdive.backend.enums.ProductType;

public class OrderItemSummaryDTO {
    // private String productName;
    private ProductType type; 
    private String details;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    // Constructors, getters, and setters
    public OrderItemSummaryDTO() {
    }

    public OrderItemSummaryDTO(ProductType type, String details, Integer quantity, BigDecimal price,
            BigDecimal subtotal) {
        this.type = type;
        this.details = details;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    // public OrderItemSummaryDTO(ProductType type, String details, Integer quantity, BigDecimal price, BigDecimal subtotal) {
    //     // this.productName = productName;
    //     this.quantity = quantity;
    //     this.price = price;
    //     this.subtotal = subtotal;
    // }

    // Getters and setters
    // public String getProductName() { return productName; }
    // public void setProductName(String productName) { this.productName = productName; }
        
    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}