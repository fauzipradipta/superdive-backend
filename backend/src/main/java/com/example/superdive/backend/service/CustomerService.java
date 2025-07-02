package com.example.superdive.backend.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.dto.CustomerDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.entity.DivingData;
import com.example.superdive.backend.entity.Reference;
import com.example.superdive.backend.exception.CustomerAlreadyExistException;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.repository.CustomerRepository;

@Service
public class CustomerService {
	
	@Autowired
	private final CustomerRepository customerRepository; 
	
	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	public Customer createCustomer(CustomerDTO customerDTO) throws CustomerAlreadyExistException{
		Customer customer = new Customer();
		customer.setName(customerDTO.getName());
		customer.setPhoneNum(customerDTO.getPhoneNum());
		customer.setDob(customerDTO.getDob());
		customer.setDiver(customerDTO.getDiver());

        List<DivingData> divingList = customerDTO.getDivingData().stream().map(d -> {
            DivingData dd = new DivingData();
            dd.setAgencyName(d.getAgencyName());
            dd.setLevel(d.getLevel());
            dd.setReference(d.getReference());
            dd.setCustomer(customer);
            return dd;
        }).collect(Collectors.toList());
        
        List<Reference> refList = Optional.ofNullable(customerDTO.getReference())
		.orElse(Collections.emptyList())
		.stream()
		.map(r -> {
			Reference ref = new Reference();
			ref.setReferenceName(r.getReferenceName());
			ref.setReferencePhoneNum(r.getReferencePhoneNum());
			ref.setCustomer(customer);
			return ref;
		})
    .collect(Collectors.toList());
        
        customer.setDivingData(divingList);
        customer.setReferences(refList);
        
    	List<Customer>existingCustomer = customerRepository.findByName(customerDTO.getName());
		if(!existingCustomer.isEmpty()) {
			throw new CustomerAlreadyExistException(customerDTO.getName() +"  Already Exist");
		}
		
		return customerRepository.save(customer);
	}
	
	//to find customer by name and phone number
	public Customer findCustomerByNameAndPhoneNum(String name, String phoneNum) throws MessageErrorException {
		
		return customerRepository.findByNameAndPhoneNum(name, phoneNum)
			   .stream()
			   .findFirst()
			   .orElseThrow(() -> new MessageErrorException(name + " with phone number " + phoneNum + " not found"));
	}
	public Customer getCustomerById(Long id) {
		return customerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
	}
	
	
	//get All Customer with pagination 
	public Page<Customer>getAllCustomerByPagination(Pageable pageable){
		return customerRepository.findAll(pageable);				
	}
	
	
	public List<Customer> getAllCustomer(){
		return customerRepository.findAll();
	}
	
	public void deleteCustomer(Long id) {
		customerRepository.deleteById(id);
	}
}
