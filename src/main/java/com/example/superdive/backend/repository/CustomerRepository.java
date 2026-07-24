package com.example.superdive.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	@Query(value = "SELECT * FROM customers WHERE name = :name", nativeQuery = true)
	List<Customer> findByName(@Param("name") String name);

	@Query(value = "SELECT * FROM customers WHERE name = :name AND phone_num = :phoneNum", nativeQuery = true)
	List<Customer> findByNameAndPhoneNum(@Param("name") String name, @Param("phoneNum") String phoneNum);

	// Customers created by a specific user
	@Query(value = "SELECT * FROM customers WHERE user_id = :userId", nativeQuery = true)
	List<Customer> findByUserId(@Param("userId") Long userId);
}
