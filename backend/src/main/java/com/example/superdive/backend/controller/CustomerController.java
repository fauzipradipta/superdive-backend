package com.example.superdive.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.superdive.backend.Entity.Customer;
import com.example.superdive.backend.Service.CustomerService;

@RestController
@RequestMapping("/api")
public class CustomerController {
	
	@Autowired
	private CustomerService customerService;
	
	@PostMapping(value = "/create-customer")
	 public Customer createCustomer(@RequestBody Customer customer) {
		
		System.out.println("Received :" + customer);
		Customer savedCustomer = customerService.createCustomer(customer);
        customerService.createCustomer(savedCustomer);
        System.out.println("Saved customer: " + savedCustomer);
        return savedCustomer;
    }
}
