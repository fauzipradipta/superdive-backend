package com.example.superdive.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleDTO {
	
	private Long id;	
	private CustomerDTO customer; 
	private List<OrderItemDTO> orderItems = new ArrayList<>();
	private BigDecimal totalPrice;
	private LocalDate orderDate;

	public CustomerDTO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}

	public List<OrderItemDTO> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemDTO> orderItems) {
		this.orderItems = orderItems;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	public SaleDTO() {
		// Default constructor
	}
	public SaleDTO(Long id, CustomerDTO customer, List<OrderItemDTO> orderItems, BigDecimal totalPrice,
			LocalDate orderDate) {
		this.id = id;
		this.customer = customer;
		this.orderItems = orderItems;
		this.totalPrice = totalPrice;
		this.orderDate = orderDate;
	}
	
	
	
}
