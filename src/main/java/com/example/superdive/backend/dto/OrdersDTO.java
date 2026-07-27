package com.example.superdive.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class OrdersDTO {
	
	private CustomerDTO customer;

	private List<OrdersItemDTO> ordersItems = new ArrayList<>();

	/* Optional on create — accepts the enum name ("PAID") or the label the UI
	 * shows ("Paid"). Left out, the order starts as UNPAID. */
	private String paymentStatus;

	public CustomerDTO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
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
