package com.example.superdive.backend.dto;

import java.math.BigDecimal;

public class SaleDTO {
	
	private CustomerDTO customer; 
//	private ProductDTO product;
	private Long productId;
	private int qty; 
	private BigDecimal totalPrice;
	public CustomerDTO getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
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
	public SaleDTO(CustomerDTO customer, Long productId, int qty, BigDecimal totalPrice) {
		super();
		this.customer = customer;
		this.productId = productId;
		this.qty = qty;
		this.totalPrice = totalPrice;
	}
	public SaleDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
