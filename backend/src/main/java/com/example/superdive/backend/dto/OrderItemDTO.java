package com.example.superdive.backend.dto;

public class OrderItemDTO {
    private Long productId;
    private int qty;
    
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public int getQty() {
        return qty;
    }
    public void setQty(int qty) {
        this.qty = qty;
    }
    public OrderItemDTO(Long productId, int qty) {
        this.productId = productId;
        this.qty = qty;
    }

    public OrderItemDTO() {
        // Default constructor
    }
}
