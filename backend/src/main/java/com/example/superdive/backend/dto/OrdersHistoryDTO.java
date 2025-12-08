package com.example.superdive.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrdersHistoryDTO {
    private Long ordersId;
    private LocalDateTime ordersDate;
    private BigDecimal totalAmount;
    private List<OrdersItemSummaryDTO> items;
    // private String status;
    // private Double totalAmount;

    // Getters and Setters
    public Long getordersId() {
        return ordersId;
    }

    public void setordersId(Long ordersId) {
        this.ordersId = ordersId;
    }

    public LocalDateTime getordersDate() {
        return ordersDate;
    }

    public void setordersDate(LocalDateTime ordersDate) {
        this.ordersDate = ordersDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrdersItemSummaryDTO> getItems() {
        return items;
    }
    public void setItems(List<OrdersItemSummaryDTO> items) {
        this.items = items;
    }
    

    // Default constructor
    public OrdersHistoryDTO() {
    }

    public OrdersHistoryDTO(Long ordersId, LocalDateTime ordersDate, BigDecimal totalAmount, List<OrdersItemSummaryDTO> items) {
        this.ordersId = ordersId;
        this.ordersDate = ordersDate;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    
}
