package com.example.superdive.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.entity.Product;

/**
 * Narrowed to FK references only.
 *
 * The catalog gRPC service owns the `product` table. This repository exists
 * solely so OrdersItem can hold its association via getReferenceById(), which
 * builds a lazy proxy from an id without reading any product column.
 *
 * Do not add query methods here. Reading product data through JPA would make
 * the backend a second reader of a table it does not own, and the two paths
 * would drift. Product reads belong in ProductService, over gRPC.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
