package com.example.superdive.backend.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.superdive.backend.dto.CustomerOrdersHistoryDTO;
import com.example.superdive.backend.dto.OrdersItemDTO;
import com.example.superdive.backend.dto.OrdersDTO;
import com.example.superdive.backend.dto.Request.PaymentStatusRequestDTO;
import com.example.superdive.backend.entity.Orders;
import com.example.superdive.backend.enums.PaymentStatus;
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
			Orders created = ordersService.createOrders(ordersDTO);
			return new ResponseEntity<>(created, HttpStatus.CREATED);
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

	@PatchMapping("/orders/{id}/payment-status")
	public ResponseEntity<?> updatePaymentStatus(@PathVariable Long id,
			@RequestBody PaymentStatusRequestDTO request) {
		try {
			Orders orders = ordersService.updatePaymentStatus(id, request.getPaymentStatus());
			return ResponseEntity.ok(orders);
		} catch (MessageErrorException e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
	}

	@GetMapping("/payment-statuses")
	public ResponseEntity<List<String>> getPaymentStatuses() {
		return ResponseEntity.ok(Arrays.stream(PaymentStatus.values())
				.map(PaymentStatus::name)
				.collect(Collectors.toList()));
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
