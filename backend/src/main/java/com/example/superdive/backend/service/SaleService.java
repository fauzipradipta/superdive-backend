package com.example.superdive.backend.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.dto.SaleDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.entity.Sale;
import com.example.superdive.backend.repository.SaleRepository;

@Service
public class SaleService {
	
	@Autowired
	private CustomerService customerService; 
	@Autowired 
	private ProductService prodService;
	@Autowired
	private SaleRepository saleRepo;

	public Sale createOrder(SaleDTO saleDTO)  {
		Customer customer = customerService.findCustomerByNameAndPhoneNum(saleDTO.getCustomer().getName(), saleDTO.getCustomer().getPhoneNum());
//		Product product = prodService.getProductById(OrderDTO.getProductId());
		Product product = prodService.createProduct(saleDTO.getProduct());
		
		Sale Order = new Sale(); 
		Order.setCustomer(customer);
		Order.setProduct(product);
		Order.setQty(saleDTO.getQty());
		Order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(saleDTO.getQty())));
		
		return saleRepo.save(Order);
		
	}

	
}
