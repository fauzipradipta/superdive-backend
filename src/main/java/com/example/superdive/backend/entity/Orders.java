package com.example.superdive.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Orders {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="orders_id")
	private Long id;
	  
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	// The user who created this order record
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
	private User user;

	@OneToMany(mappedBy="orders",cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonManagedReference
	private List <OrdersItem> items = new ArrayList<>();

	private BigDecimal totalPrice; 
	@Column(name="orders_date")
	private LocalDateTime ordersDate;

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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<OrdersItem> getItems() {
		return items;
	}
	public void setItems(List<OrdersItem> items) {
		this.items = items;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public LocalDateTime getordersDate() {
		return ordersDate;
	}
	public void setordersDate(LocalDateTime ordersDate) {
		this.ordersDate = ordersDate;
	}
	public Orders(Long id, Customer customer, List<OrdersItem> items, BigDecimal totalPrice, LocalDateTime ordersDate) {
		this.id = id;
		this.customer = customer;
		this.items = items;
		this.totalPrice = totalPrice;
		this.ordersDate = ordersDate;
	}

	public Orders() {
		// Default constructor
	}

	public void addItem(OrdersItem item) {
        items.add(item);
        item.setorders(this);
    }
    
	public void calculateTotal() {
		BigDecimal total = this.items.stream()
			.map(item -> item.getPrice().multiply(new BigDecimal(item.getQty())))
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		this.setTotalPrice(total);
	}
}