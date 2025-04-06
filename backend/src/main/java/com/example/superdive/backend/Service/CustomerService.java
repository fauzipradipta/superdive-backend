package com.example.superdive.backend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.Entity.Customer;
import com.example.superdive.backend.Entity.DivingData;
import com.example.superdive.backend.Entity.Reference;
import com.example.superdive.backend.Repository.CustomerRepository;
import com.example.superdive.backend.dto.CustomerDTO;
import com.example.superdive.backend.exception.CustomerAlreadyExistException;

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
		

        List<DivingData> divingList = customerDTO.getDivingData().stream().map(d -> {
            DivingData dd = new DivingData();
            dd.setAgencyName(d.getAgencyName());
            dd.setLevel(d.getLevel());
            dd.setReference(d.isReference());
            dd.setCustomer(customer);
            return dd;
        }).collect(Collectors.toList());
        
        List<Reference> refList = customerDTO.getReference().stream().map(r -> {
            Reference ref = new Reference();
            ref.setReferenceName(r.getReferenceName());
            ref.setReferencePhoneNum(r.getPhoneNum());
            ref.setCustomer(customer); // link back
            return ref;
        }).collect(Collectors.toList());
        
        customer.setDivingData(divingList);
        customer.setReferences(refList);
        
    	Optional<Customer>existingCustomer = customerRepository.findByName(customer.getName());
		if(existingCustomer.isPresent()) {
			throw new CustomerAlreadyExistException(customer.getName() +"  Already Exist");
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
