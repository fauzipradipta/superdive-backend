package com.example.superdive.backend.entity;

import java.math.BigDecimal;

import com.example.superdive.backend.enums.ProductType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")

public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="product_id")
	private Long id;
	private String name;
	private ProductType type;
	private String details;
	private BigDecimal price;
	@ManyToOne
	private Customer customer;
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	
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
	public Product(Long id,String name, ProductType type, String details, BigDecimal price, Customer customer) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.details = details;
		this.price = price;
		this.customer = customer;
	}
	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
    
    
}
