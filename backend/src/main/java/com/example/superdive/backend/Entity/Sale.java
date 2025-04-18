package com.example.superdive.backend.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Sale {
	
	private Long id; 
	private int qty; 
	private BigDecimal totalPrice;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Sale(Long id, int qty, BigDecimal totalPrice) {
		super();
		this.id = id;
		this.qty = qty;
		this.totalPrice = totalPrice;
	}
	public Sale() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@ManyToOne
	private Customer customer; 
	
	@ManyToOne
	private Product product;
}
