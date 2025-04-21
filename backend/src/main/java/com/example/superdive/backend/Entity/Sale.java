package com.example.superdive.backend.Entity;

import java.math.BigDecimal;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Sale {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	private int qty; 
	private BigDecimal totalPrice;
	@ManyToOne
	private Customer customer; 
	
	@ManyToOne
	private Product product;
	
	
	public Sale(Long id, int qty, BigDecimal totalPrice, Customer customer, Product product) {
		super();
		this.id = id;
		this.qty = qty;
		this.totalPrice = totalPrice;
		this.customer = customer;
		this.product = product;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
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
	
	public Sale() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
