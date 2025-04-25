package com.example.superdive.backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.superdive.backend.dto.OrderDTO;
import com.example.superdive.backend.service.OrderService;

@Controller
@RequestMapping("/api")
@CrossOrigin("localhost:3000")
public class OrderController {
	
	@Autowired
	private  OrderService orderService; 
	
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
	
	@PostMapping(value = "/create-sale")
	public ResponseEntity<String> createSale(@RequestBody OrderService orderService){
		orderService.creatOrder(orderService);
		
		return ResponseEntity.ok("Customer Has been published");
	}
}
