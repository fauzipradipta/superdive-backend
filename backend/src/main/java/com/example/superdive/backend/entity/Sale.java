package com.example.superdive.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
//import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
//import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Sale {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="sale_id")
	private Long id;
	  
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToMany(mappedBy="sale",cascade=CascadeType.ALL, orphanRemoval=true)
	private List <OrderItem> items = new ArrayList<>();

	private BigDecimal totalPrice; 
	private LocalDateTime orderDate;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public List<OrderItem> getItems() {
		return items;
	}
	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public LocalDateTime getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}
	public Sale(Long id, Customer customer, List<OrderItem> items, BigDecimal totalPrice, LocalDateTime orderDate) {
		this.id = id;
		this.customer = customer;
		this.items = items;
		this.totalPrice = totalPrice;
		this.orderDate = orderDate;
	}

	public Sale() {
		// Default constructor
	}

	public void addItem(OrderItem item) {
        items.add(item);
        item.setSale(this);
    }
    
	public void calculateTotal() {
		BigDecimal total = this.items.stream()
			.map(item -> item.getPrice().multiply(new BigDecimal(item.getQty())))
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		this.setTotalPrice(total);
	}
}