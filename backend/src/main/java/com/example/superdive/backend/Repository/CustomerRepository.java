package com.example.superdive.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.Entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository< Customer,Long> {
	Optional<Customer> findByName(String Name);
}
