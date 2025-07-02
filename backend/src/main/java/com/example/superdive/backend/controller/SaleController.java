package com.example.superdive.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.superdive.backend.dto.SaleDTO;
import com.example.superdive.backend.entity.Sale;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.service.SaleService;


@Controller
@RequestMapping("/api")
@CrossOrigin("localhost:3000")
public class SaleController {
	
	@Autowired
	private SaleService orderService; 
	
	public SaleController(SaleService orderService) {
		this.orderService = orderService;
	}
	
	@PostMapping(value = "/create-sale")
	public ResponseEntity<String> createSale(@RequestBody SaleDTO orderDTO){
		
		try{
			orderService.createOrder(orderDTO);
			return ResponseEntity.ok("Order successfully recorded");
		}
		catch (MessageErrorException e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
		
		
	}

	// @GetMapping(value = "/all-sales")
	// public ResponseEntity<List<Sale>> getAllSale() {
	// 	List<Sale> sales = orderService.getAllOrder();

	// 	if (sales.isEmpty()) {
	// 		return ResponseEntity.noContent().build();
	// 	}
	// 	return ResponseEntity.ok(sales);
	// }


}
