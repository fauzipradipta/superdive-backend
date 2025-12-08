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
import com.example.superdive.backend.dto.OrderDTO;
import com.example.superdive.backend.entity.Order;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.service.OrderService;


@Controller
@RequestMapping("/api")
@CrossOrigin("localhost:3000")
public class OrderController {
	
	@Autowired
	private OrderService OrderService; 
	
	public OrderController(OrderService OrderService) {
		this.OrderService = OrderService;
	}
	
	@PostMapping(value = "/create-Order")
	public ResponseEntity<String> createOrder(@RequestBody OrderDTO orderDTO){
		
		try{
			OrderService.createOrder(orderDTO);
			return ResponseEntity.ok("Order successfully recorded");
		}
		catch (MessageErrorException e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
		
		
	}

	@PostMapping(value = "/{orderId}/items")
	public ResponseEntity<Order>addProductToOrder(@PathVariable Long orderId, @RequestBody OrderItemDTO orderDTO) {
		try {
			Order Order = OrderService.addProductToOrder(orderId, orderDTO);
			return ResponseEntity.ok(Order);
		} catch (MessageErrorException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	 @GetMapping("/order/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        try {
            Order order = OrderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (MessageErrorException e) {
            return ResponseEntity.notFound().build();
        }
    }
	
	@GetMapping(value = "/all-Orders")
	public ResponseEntity<List<Order>> getAllOrder() {		
		List<Order>Orders = OrderService.getAllOrders(); 
		return ResponseEntity.ok(Orders);
	}
	
	@GetMapping("/customer-Order-history/{customerId}")
	public ResponseEntity<?> getCustomerOrderHistory(@PathVariable Long customerId) {
    try {
        CustomerOrderHistoryDTO orderHistory = OrderService.getCustomerOrderHistoryDTO(customerId);
        return ResponseEntity.ok(orderHistory);
    } catch (MessageErrorException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}}
