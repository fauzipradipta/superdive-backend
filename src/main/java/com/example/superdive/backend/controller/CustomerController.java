package com.example.superdive.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.superdive.backend.dto.CustomerDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.exception.CustomerAlreadyExistException;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.service.CustomerService;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api")
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
	public ResponseEntity<Customer> getCustomerById(@PathVariable Long id ) {
		Customer customer = customerService.getCustomerById(id);
		if (customer == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(customer);
	}

	@GetMapping(value="/all-customer")
	public ResponseEntity<Map<String,Object>> getAllCustomer(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "50") int limit) {

		Pageable pageable = PageRequest.of(page - 1, limit);
		Page<Customer> customerPage = customerService.getAllCustomerByPagination(pageable);

		Map<String, Object> response = new HashMap<>();
		response.put("data", customerPage.getContent());
		response.put("currentPage", customerPage.getNumber() + 1);
		response.put("totalItems", customerPage.getTotalElements());
		response.put("totalPages", customerPage.getTotalPages());

		return ResponseEntity.ok(response);
	}

    @PutMapping(value="/customers/update-customer/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok(updatedCustomer);
        } catch (MessageErrorException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
