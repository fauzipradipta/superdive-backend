package com.example.superdive.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.Entity.Customer;
import com.example.superdive.backend.Entity.Sale;
import com.example.superdive.backend.Repository.CustomerRepository;
import com.example.superdive.backend.Repository.ProductRepository;
import com.example.superdive.backend.Repository.SaleRepository;
import com.example.superdive.backend.dto.SaleDTO;

@Service
public class SaleService {
	
	@Autowired
	private CustomerRepository customerRepo; 
	@Autowired 
	private ProductRepository prodoctRepo;
	@Autowired
	private SaleRepository saleRepo;
	

	
}
