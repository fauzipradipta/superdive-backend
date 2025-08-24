package com.example.superdive.backend.dto;


public class CustomerOrderHistoryDTO() {
    private Long customerId; 
    private String customerName;
    private String phoneNum;
    private List<OrderHistoryDTO>orders; 
    private BigDecimal totalPrice; 
    private Integer totalItems;

    
    // Default constructor
}