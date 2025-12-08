package com.example.superdive.backend.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.enums.ProductType;

@Repository
public interface ProductRepository extends JpaRepository< Product,Long> {
	@Query("Select s from Order s WHERE s.product.productType = :productType")
	List<Product> findByType(ProductType type);

	
}
