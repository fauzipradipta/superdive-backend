package com.example.superdive.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.superdive.backend.entity.Orders;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT o FROM Orders o ORDER BY o.ordersDate DESC")
    List<Orders> findAllOrdersByDate();

    List<Orders> findByCustomerId(Long customerId);

    @Query("SELECT s FROM Orders s " +
            "LEFT JOIN FETCH s.items i " +
            "LEFT JOIN FETCH i.product p " +
            "LEFT JOIN FETCH s.customer c " +
            "WHERE s.id = :id")
    Optional<Orders> findByIdWithItemsAndProducts(@Param("id") Long id);

    @Query("SELECT s FROM Orders s " +
            "LEFT JOIN FETCH s.items i " +
            "LEFT JOIN FETCH i.product p " +
            "WHERE s.customer.id = :customerId")
    List<Orders> findByCustomerIdWithItemsAndProducts(@Param("customerId") Long customerId);
}
