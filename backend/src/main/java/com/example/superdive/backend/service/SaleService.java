package com.example.superdive.backend.service;

import java.math.BigDecimal;

import java.util.List;
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

	public Sale creatSale(SaleDTO saleDTO)  {
		Customer customer = customerService.findCustomerByNameAndPhoneNum(saleDTO.getCustomer().getName(), saleDTO.getCustomer().getPhoneNum());
		Product product = prodService.getProductById(saleDTO.getProductId());
		
		Sale sale = new Sale(); 
		sale.setCustomer(customer);
		sale.setProduct(product);
		sale.setQty(saleDTO.getQty());
		sale.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(saleDTO.getQty())));
		
		return saleRepo.save(sale);
		
	}

	
}
