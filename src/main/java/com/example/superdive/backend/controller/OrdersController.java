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

import com.example.superdive.backend.dto.CustomerOrdersHistoryDTO;
import com.example.superdive.backend.dto.OrdersItemDTO;
import com.example.superdive.backend.dto.OrdersDTO;
import com.example.superdive.backend.entity.Orders;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.service.OrdersService;

@Controller
@RequestMapping("/api")
@CrossOrigin("localhost:3000")
public class OrdersController {

	@Autowired
	private OrdersService ordersService;

	public OrdersController(OrdersService ordersService) {
		this.ordersService = ordersService;
	}

	@PostMapping(value = "/create-orders")
	public ResponseEntity<?> createOrders(@RequestBody OrdersDTO ordersDTO) {

		try {
			ordersService.createOrders(ordersDTO);
			return ResponseEntity.ok("orders successfully recorded");
		} catch (MessageErrorException e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}

	}

	@PostMapping(value = "/{ordersId}/items")
	public ResponseEntity<Orders> addProductToOrders(@PathVariable Long ordersId,
			@RequestBody OrdersItemDTO OrdersDTO) {
		try {
			Orders orders = ordersService.addProductToOrders(ordersId, OrdersDTO);
			return ResponseEntity.ok(orders);
		} catch (MessageErrorException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@GetMapping("/orders/{id}")
	public ResponseEntity<Orders> getOrders(@PathVariable Long id) {
		try {
			Orders orders = ordersService.getOrdersById(id);
			return ResponseEntity.ok(orders);
		} catch (MessageErrorException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping(value = "/all-orders")
	public ResponseEntity<List<Orders>> getAllOrders() {
		List<Orders> orders = ordersService.getAllOrders();
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/customer-orders-history/{customerId}")
	public ResponseEntity<?> getCustomerOrdersHistory(@PathVariable Long customerId) {
		try {
			CustomerOrdersHistoryDTO ordersHistory = ordersService.getCustomerOrdersHistoryDTO(customerId);
			return ResponseEntity.ok(ordersHistory);
		} catch (MessageErrorException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}
