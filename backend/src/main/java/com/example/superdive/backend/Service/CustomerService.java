package com.example.superdive.backend.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.Entity.Customer;
import com.example.superdive.backend.Repository.CustomerRepository;
import com.example.superdive.backend.exception.CustomerAlreadyExistException;

@Service
public class CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository; 
	
	public Customer createCustomer(Customer customer) throws CustomerAlreadyExistException{
		Optional<Customer>existingCustomer = customerRepository.findByName(customer.getName());
		if(existingCustomer.isPresent()) {
			throw new CustomerAlreadyExistException(customer.getName() +"Already Exist");
		}
		
        return customerRepository.save(customer);
    }
	
	public Customer getCustomerById(Long id) {
		return customerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
	}
	
	public List<Customer> getAllCustomer(){
		return customerRepository.findAll();
	}
	
	public void deleteCustomer(Long id) {
		customerRepository.deleteById(id);
	}
}
