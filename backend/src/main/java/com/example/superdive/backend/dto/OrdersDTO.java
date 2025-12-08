package com.example.superdive.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class OrdersDTO {
	
	private CustomerDTO customer; 

	private List<OrdersItemDTO> ordersItems = new ArrayList<>();

	public CustomerDTO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}

	public List<OrdersItemDTO> getordersItems() {
		return ordersItems;
	}

	public void setordersItems(List<OrdersItemDTO> ordersItems) {
		this.ordersItems = ordersItems;
	}
	
	public OrdersDTO() {
		// Default constructor
	}
	
	
	
}
