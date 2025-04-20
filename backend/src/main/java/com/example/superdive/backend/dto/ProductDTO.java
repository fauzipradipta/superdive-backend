package com.example.superdive.backend.dto;

import java.math.BigDecimal;

public class ProductDTO {
	
	private Long id;
	private String categories;
	private String details;
	private BigDecimal price;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCategories() {
		return categories;
	}
	public void setCategories(String categories) {
		this.categories = categories;
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
	public ProductDTO(Long id, String categories, String details, BigDecimal price) {
		super();
		this.id = id;
		this.categories = categories;
		this.details = details;
		this.price = price;
	}
	public ProductDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
