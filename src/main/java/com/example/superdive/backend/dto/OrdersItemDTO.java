package com.example.superdive.backend.dto;

import java.math.BigDecimal;

public class OrdersItemDTO {
    private Long productId;
    private ProductDTO productDTO; 
    private Integer qty;
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
    public OrdersItemDTO(Long productId, int qty) {
        this.productId = productId;
        this.qty = qty;
    }

    public OrdersItemDTO() {
        // Default constructor
    }
    
}
