package com.example.superdive.backend.dto;

import java.math.BigDecimal;

public class SaleDTO {
	
	private CustomerDTO customer; 
	private ProductDTO product;
	private int qty; 
	private BigDecimal totalPrice;
	public CustomerDTO getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}
	public ProductDTO getProduct() {
		return product;
	}
	public void setProduct(ProductDTO product) {
		this.product = product;
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
	public SaleDTO(CustomerDTO customer, ProductDTO product, int qty, BigDecimal totalPrice) {
		super();
		this.customer = customer;
		this.product = product;
		this.qty = qty;
		this.totalPrice = totalPrice;
	}
	public SaleDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
