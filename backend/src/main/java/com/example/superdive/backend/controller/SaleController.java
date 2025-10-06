package com.example.superdive.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.superdive.backend.dto.CustomerOrderHistoryDTO;
import com.example.superdive.backend.dto.OrderItemDTO;
import com.example.superdive.backend.dto.SaleDTO;
import com.example.superdive.backend.entity.Sale;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.service.SaleService;


@Controller
@RequestMapping("/api")
@CrossOrigin("localhost:3000")
public class SaleController {
	
	@Autowired
	private SaleService saleService; 
	
	public SaleController(SaleService saleService) {
		this.saleService = saleService;
	}
	
	@PostMapping(value = "/create-sale")
	public ResponseEntity<String> createSale(@RequestBody SaleDTO orderDTO){
		
		try{
			saleService.createOrder(orderDTO);
			return ResponseEntity.ok("Order successfully recorded");
		}
		catch (MessageErrorException e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
		
		
	}

	@PostMapping(value = "/{orderId}/items")
	public ResponseEntity<Sale>addProductToOrder(@PathVariable Long orderId, @RequestBody OrderItemDTO orderDTO) {
		try {
			Sale sale = saleService.addProductToOrder(orderId, orderDTO);
			return ResponseEntity.ok(sale);
		} catch (MessageErrorException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	 @GetMapping("/order/{id}")
    public ResponseEntity<Sale> getOrder(@PathVariable Long id) {
        try {
            Sale order = saleService.getSaleById(id);
            return ResponseEntity.ok(order);
        } catch (MessageErrorException e) {
            return ResponseEntity.notFound().build();
        }
    }
	
	@GetMapping(value = "/all-sales")
	public ResponseEntity<List<Sale>> getAllSale() {		
		List<Sale>sales = saleService.getAllSales(); 
		return ResponseEntity.ok(sales);
	}
	
	@GetMapping("/customer-sale-history/{customerId}")
	public ResponseEntity<?> getCustomerOrderHistory(@PathVariable Long customerId) {
    try {
        CustomerOrderHistoryDTO orderHistory = saleService.getCustomerOrderHistoryDTO(customerId);
        return ResponseEntity.ok(orderHistory);
    } catch (MessageErrorException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}}
