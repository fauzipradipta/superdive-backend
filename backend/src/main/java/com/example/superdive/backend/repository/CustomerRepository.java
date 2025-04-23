package com.example.superdive.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository< Customer,Long> {
	List<Customer> findByName(String Name);
	
	List<Customer> findByNameAndPhoneNum(String name, String phoneNum);
}
