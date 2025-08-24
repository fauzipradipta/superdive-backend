package com.example.superdive.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class SaleDTO {
	
	private CustomerDTO customer; 

	private List<OrderItemDTO> orderItems = new ArrayList<>();

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
	
	public SaleDTO() {
		// Default constructor
	}
	
	
	
}
