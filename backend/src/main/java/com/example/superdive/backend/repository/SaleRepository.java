package com.example.superdive.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.superdive.backend.entity.Sale;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s ORDER BY s.orderDate DESC")
    List<Sale> findAllOrderByDate();

    List<Sale> findByCustomerId(Long customerId);

    @Query("SELECT s FROM Sale s " +
            "LEFT JOIN FETCH s.items i " +
            "LEFT JOIN FETCH i.product p " +
            "LEFT JOIN FETCH s.customer c " +
            "WHERE s.id = :id")
    Optional<Sale> findByIdWithItemsAndProducts(@Param("id") Long id);

    @Query("SELECT s FROM Sale s " +
            "LEFT JOIN FETCH s.items i " +
            "LEFT JOIN FETCH i.product p " +
            "WHERE s.customer.id = :customerId")
    List<Sale> findByCustomerIdWithItemsAndProducts(@Param("customerId") Long customerId);
}
