package com.example.superdive.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.enums.ProductType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	// Product.type has no @Enumerated, so JPA persists it as the enum ORDINAL (int).
	// Bind #type.ordinal() via SpEL so the native comparison matches the stored value.
	@Query(value = "SELECT * FROM product WHERE type = :#{#type.ordinal()}", nativeQuery = true)
	List<Product> findByType(@Param("type") ProductType type);

}
