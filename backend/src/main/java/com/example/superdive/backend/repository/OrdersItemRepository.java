package com.example.superdive.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.superdive.backend.entity.OrdersItem;

public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {
    
}
