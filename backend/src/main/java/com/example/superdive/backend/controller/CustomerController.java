package com.example.superdive.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.superdive.backend.dto.CustomerDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.exception.CustomerAlreadyExistException;
import com.example.superdive.backend.service.CustomerService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins ="http://localhost:3000")
public class CustomerController {
	
	@Autowired
	private final CustomerService customerService;
	
	public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
	
	@PostMapping(value = "/create-customer")
	public ResponseEntity<?> createCustomer(@RequestBody CustomerDTO customerDTO) {
        try {
            Customer savedCustomer = customerService.createCustomer(customerDTO);
            return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
        } catch (CustomerAlreadyExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
	
	@GetMapping(value="/customers/{id}")
	public Customer getCustomerById(@PathVariable Long id ) {
		return customerService.getCustomerById(id);
	}
	
	@GetMapping(value="/all-customer")
	public List<Customer> getAllCustomerById(){
		return customerService.getAllCustomer();
	}
}
