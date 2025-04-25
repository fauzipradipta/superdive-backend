package com.example.superdive.backend.service;

import java.math.BigDecimal;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.dto.OrderDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.entity.Order;
import com.example.superdive.backend.repository.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private CustomerService customerService; 
	@Autowired 
	private ProductService prodService;
	@Autowired
	private OrderRepository OrderRepo;

	public Order creatOrder(OrderDTO OrderDTO)  {
		Customer customer = customerService.findCustomerByNameAndPhoneNum(OrderDTO.getCustomer().getName(), OrderDTO.getCustomer().getPhoneNum());
		Product product = prodService.getProductById(OrderDTO.getProductId());
		
		Order Order = new Order(); 
		Order.setCustomer(customer);
		Order.setProduct(product);
		Order.setQty(OrderDTO.getQty());
		Order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(OrderDTO.getQty())));
		
		return OrderRepo.save(Order);
		
	}

	
}
