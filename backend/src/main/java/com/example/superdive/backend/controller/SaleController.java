package com.example.superdive.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.superdive.backend.Service.SaleService;
import com.example.superdive.backend.dto.SaleDTO;

@Controller
@RequestMapping("/api")
@CrossOrigin("localhost:3000")
public class SaleController {
	
	private  SaleService saleService; 
	
	@PostMapping(value = "/create-sale")
	public ResponseEntity<String> createSale(@RequestBody SaleDTO saleDTO){
		saleService.creatSale(saleDTO);
		
		return ResponseEntity.ok("Customer Has been published");
	}
}
