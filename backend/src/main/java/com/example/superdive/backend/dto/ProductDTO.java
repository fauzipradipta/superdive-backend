package com.example.superdive.backend.dto;

import java.math.BigDecimal;

import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.enums.ProductType;

public class ProductDTO {
	
	private Long id;
	private ProductType type;
	private String details;
	private BigDecimal price;
	private Product product;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public ProductDTO(Long id, ProductType type, String details, BigDecimal price, Product product) {
		super();
		this.id = id;
		this.type = type;
		this.details = details;
		this.price = price;
		this.product=product;
	}
	public ProductDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
