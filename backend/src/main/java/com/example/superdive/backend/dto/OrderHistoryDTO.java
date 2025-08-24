package com.example.superdive.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderHistoryDTO {
    private Long orderId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private List<OrderItemHitoryDTO> items;
    // private String status;
    // private Double totalAmount;

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemHitoryDTO> getItems() {
        return items;
    }
    public void setItems(List<OrderItemHitoryDTO> items) {
        this.items = items;
    }
    

    // Default constructor
    public OrderHistoryDTO() {
    }

    public OrderHistoryDTO(Long orderId, LocalDateTime orderDate, BigDecimal totalAmount), List<OrderItemHitoryDTO> items {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    
}
