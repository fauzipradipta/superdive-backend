package com.example.superdive.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class CustomerOrdersHistoryDTO {
    private Long customerId; 
    private String customerName;
    private String phoneNum;
    private List<OrdersHistoryDTO> orders; 
    private BigDecimal totalPrice; 
    private Integer totalItems;

    // Default constructor
    public CustomerOrdersHistoryDTO() {
        // Empty constructor
    }
    
    // Parameterized constructor
    public CustomerOrdersHistoryDTO(Long customerId, String customerName, String phoneNum, 
                                   List<OrdersHistoryDTO> orders, BigDecimal totalPrice, Integer totalItems) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phoneNum = phoneNum;
        this.orders = orders;
        this.totalPrice = totalPrice;
        this.totalItems = totalItems;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public List<OrdersHistoryDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrdersHistoryDTO> orders) {
        this.orders = orders;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
} // <-- THIS CLOSING BRACE WAS MISSING