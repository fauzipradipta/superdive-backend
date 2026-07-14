package com.example.superdive.backend.dto;

import java.math.BigDecimal;

import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.enums.ProductType;

public class ProductDTO {

	private Long id;
	private String name;
	private ProductType type;
	private String details;
	private BigDecimal price;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	public ProductDTO(Long id, String name, ProductType type, String details, BigDecimal price) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.details = details;
		this.price = price;
	}

	public ProductDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

}
