package com.example.superdive.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class CustomerOrderHistoryDTO {
    private Long customerId; 
    private String customerName;
    private String phoneNum;
    private List<OrderHistoryDTO> orders; 
    private BigDecimal totalPrice; 
    private Integer totalItems;
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
    public List<OrderHistoryDTO> getOrders() {
        return orders;
    }
    public void setOrders(List<OrderHistoryDTO> orders) {
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
    public CustomerOrderHistoryDTO(Long customerId, String customerName, String phoneNum, List<OrderHistoryDTO> orders,
            BigDecimal totalPrice, Integer totalItems) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phoneNum = phoneNum;
        this.orders = orders;
        this.totalPrice = totalPrice;
        this.totalItems = totalItems;
    }

    

    // Default constructor
}