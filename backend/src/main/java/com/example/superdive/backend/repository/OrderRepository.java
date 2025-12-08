package com.example.superdive.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.superdive.backend.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT s FROM Order s ORDER BY s.orderDate DESC")
    List<Order> findAllOrderByDate();

    List<Order> findByCustomerId(Long customerId);

    @Query("SELECT s FROM Order s " +
            "LEFT JOIN FETCH s.items i " +
            "LEFT JOIN FETCH i.product p " +
            "LEFT JOIN FETCH s.customer c " +
            "WHERE s.id = :id")
    Optional<Order> findByIdWithItemsAndProducts(@Param("id") Long id);

    @Query("SELECT s FROM Order s " +
            "LEFT JOIN FETCH s.items i " +
            "LEFT JOIN FETCH i.product p " +
            "WHERE s.customer.id = :customerId")
    List<Order> findByCustomerIdWithItemsAndProducts(@Param("customerId") Long customerId);
}
