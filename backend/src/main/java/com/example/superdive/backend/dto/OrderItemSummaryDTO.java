package com.example.superdive.backend.dto;

import java.math.BigDecimal;

import com.example.superdive.backend.entity.Product;

// import com.example.superdive.backend.enums.String;

public class OrderItemSummaryDTO {
    private Product product;
    private String type; 
    private String details;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    // Constructors, getters, and setters
    public OrderItemSummaryDTO() {
    }

    public OrderItemSummaryDTO(Product product, String type, String details, 
            Integer quantity, BigDecimal price, BigDecimal subtotal) {
			this.product = product;
			this.type = type;
			this.details = details;
			this.quantity = quantity;
			this.price = price;
			this.subtotal = subtotal;
	}

   

    // Getters and setters
    // public String getProductName() { return productName; }
    // public void setProductName(String productName) { this.productName = productName; }
    
    
        
    public String getType() {
        return type;
    }

    public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}