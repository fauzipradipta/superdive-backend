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

        // Native: all orders, newest first.
        @Query(value = "SELECT * FROM orders ORDER BY orders_date DESC", nativeQuery = true)
        List<Orders> findAllOrdersByDate();

        // Native: orders for one customer.
        @Query(value = "SELECT * FROM orders WHERE customer_id = :customerId", nativeQuery = true)
        List<Orders> findByCustomerId(@Param("customerId") Long customerId);

        // Native: orders created by a specific user.
        @Query(value = "SELECT * FROM orders WHERE user_id = :userId", nativeQuery = true)
        List<Orders> findByUserId(@Param("userId") Long userId);

        // Kept as JPQL on purpose: native SQL cannot fetch-hydrate the @OneToMany
        // `items` / `product` collections into the returned entity, and callers read
        // those collections outside a transaction (see OrdersService). Converting
        // these to native would leave the collections empty / throw
        // LazyInitializationException.
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

        @Query("SELECT DISTINCT o FROM Orders o " +
                        "LEFT JOIN FETCH o.customer c " +
                        "LEFT JOIN FETCH o.items i " +
                        "LEFT JOIN FETCH i.product p " +
                        "ORDER BY o.ordersDate DESC")
        List<Orders> findAllOrdersWithCustomerAndItems();
}
