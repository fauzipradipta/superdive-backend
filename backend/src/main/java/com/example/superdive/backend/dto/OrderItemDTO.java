package com.example.superdive.backend.dto;

import java.math.BigDecimal;

public class OrderItemDTO {
    private Long productId;
    private ProductDTO productDTO; 
    private int qty;
    private BigDecimal price; 

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public ProductDTO getProductDTO() {
        return productDTO;
    }

    public void setProductDTO(ProductDTO productDTO) {
        this.productDTO = productDTO;
    }
    
    public int getQty() {
        return qty;
    }
    public void setQty(int qty) {
        this.qty = qty;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public OrderItemDTO(Long productId, int qty) {
        this.productId = productId;
        this.qty = qty;
    }

    public OrderItemDTO() {
        // Default constructor
    }
    
}
