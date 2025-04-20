package com.example.superdive.backend.Service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.Entity.Customer;
import com.example.superdive.backend.Entity.Product;
import com.example.superdive.backend.Entity.Sale;
import com.example.superdive.backend.Repository.SaleRepository;
import com.example.superdive.backend.dto.SaleDTO;

@Service
public class SaleService {
	
	@Autowired
	private CustomerService customerService; 
	@Autowired 
	private ProductService prodService;
	@Autowired
	private SaleRepository saleRepo;

	public Sale creatSale(SaleDTO saleDTO) {
		Customer customer = customerService.createCustomer(saleDTO.getCustomer());
		Product product = prodService.getProductById(saleDTO.getProductId());
		
		Sale sale = new Sale(); 
		sale.setCustomer(customer);
		sale.setProduct(product);
		sale.setQty(saleDTO.getQty());
		sale.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
		
		return saleRepo.save(sale);
		
	}

	
}
