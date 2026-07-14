package com.example.superdive.backend.dto;

import java.math.BigDecimal;

// import com.example.superdive.backend.enums.String;

public class OrdersItemSummaryDTO {
    private Long productId;
    private String name;
    private String type;
    private String details;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    // Constructors, getters, and setters
    public OrdersItemSummaryDTO() {
    }

    public OrdersItemSummaryDTO(Long productId, String name, String type, String details,
            Integer quantity, BigDecimal price, BigDecimal subtotal) {
        this.productId = productId;
        this.name = name;
        this.type = type;
        this.details = details;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    // Getters and setters
    // public String getProductName() { return productName; }
    // public void setProductName(String productName) { this.productName =
    // productName; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}