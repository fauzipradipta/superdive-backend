package com.example.superdive.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.superdive.backend.dto.SaleDTO;
import com.example.superdive.backend.service.SaleService;

@Controller
@RequestMapping("/api")
@CrossOrigin("localhost:3000")
public class SaleController {
	
	@Autowired
	private  SaleService saleService; 
	
	public SaleController(SaleService saleService) {
		this.saleService = saleService;
	}
	
	@PostMapping(value = "/create-sale")
	public ResponseEntity<String> createSale(@RequestBody SaleDTO saleDTO){
		saleService.creatSale(saleDTO);
		
		return ResponseEntity.ok("Customer Has been published");
	}
}
