package com.example.superdive.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.dto.CustomerOrderHistoryDTO;
import com.example.superdive.backend.dto.OrderHistoryDTO;
import com.example.superdive.backend.dto.OrderItemDTO;
import com.example.superdive.backend.dto.OrderItemSummaryDTO;
import com.example.superdive.backend.dto.OrderDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.entity.OrderItem;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.entity.Order;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.repository.OrderRepository;

import jakarta.transaction.Transactional;

import com.example.superdive.backend.repository.OrderItemRepository;
@Service
public class OrderService {
	
	@Autowired
	private CustomerService customerService; 
	@Autowired 
	private ProductService prodService;
	@Autowired
	private OrderRepository OrderRepo;
	@Autowired
	private OrderItemRepository orderItemRepo;

	

	@Transactional
	public Order createOrder(OrderDTO OrderDTO) throws MessageErrorException {
		
		
	    if (OrderDTO.getOrderItems() == null || OrderDTO.getOrderItems().isEmpty()) {
	        throw new MessageErrorException("Order must contain at least one item.");
	    }

	    Customer customer = customerService.findCustomerByNameAndPhoneNum(
	        OrderDTO.getCustomer().getName(),
	        OrderDTO.getCustomer().getPhoneNum()
	    );

	    Order Order = new Order();
	    Order.setCustomer(customer);
	    Order.setOrderDate(LocalDateTime.now());

	    for (OrderItemDTO itemDTO : OrderDTO.getOrderItems()) {
	        if (itemDTO.getQty() == null || itemDTO.getQty() <= 0) {
	            throw new MessageErrorException("Invalid quantity for product");
	        }

	        Product product = prodService.createProduct(itemDTO.getProductDTO());

	        OrderItem item = new OrderItem();
	        item.setProduct(product);
	        item.setQty(itemDTO.getQty());
	        item.setPrice(itemDTO.getPrice());
	        item.setOrder(Order); 
	        
	        
	        Order.addItem(item); 
		}

		Order.calculateTotal();
		return OrderRepo.save(Order); 
	}

	@Transactional
	public Order addProductToOrder(Long OrderId, OrderItemDTO itemDTO) throws MessageErrorException {
		Order Order = OrderRepo.findByIdWithItemsAndProducts(OrderId)
				.orElseThrow(() -> new MessageErrorException("Order not found with id: " + OrderId));

		Product product = prodService.getProductById(itemDTO.getProductId());

		Optional<OrderItem> existingItem = Order.getItems().stream()
				.filter(item -> item.getProduct().getId().equals(product.getId()))
				.findFirst();

		if (existingItem.isPresent()) {
			OrderItem item = existingItem.get();
			item.setQty(item.getQty() + itemDTO.getQty());
		} else {
			OrderItem item = new OrderItem();
			item.setProduct(product);
			item.setQty(itemDTO.getQty());
			item.setPrice(itemDTO.getPrice()); 
			item.setOrder(Order);
			Order.addItem(item);

			System.out.println("Item Order reference: " + item.getOrder());
			System.out.println("Order items count: " + Order.getItems().size());
		}

		Order.calculateTotal();

		Order savedOrder = OrderRepo.save(Order);
		System.out.println("Saved Order ID: " + savedOrder.getId());
		System.out.println("Saved Order items count: " + savedOrder.getItems().size());
		return savedOrder;

	}

	public Order getOrderById(Long id) throws MessageErrorException {
		return OrderRepo.findById(id)
			.orElseThrow(() -> new MessageErrorException("Order not found" ));
	}

	public List<Order> getAllOrders() {
		return OrderRepo.findAll();
	}

	public List<Order> getOrdersByCustomerId(Long customerId) {
		return OrderRepo.findByCustomerId(customerId);
	}

	public CustomerOrderHistoryDTO getCustomerOrderHistoryDTO(Long customerId) throws MessageErrorException{

		
		Customer customer = customerService.getCustomerById(customerId);

		
		List<Order> Orders = OrderRepo.findByCustomerIdWithItemsAndProducts(customerId);

		if(Orders.isEmpty()){
			throw new MessageErrorException("No orders found for this customer");
		}

		
		BigDecimal totalSpent = Orders.stream()
			.map(Order::getTotalPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		
		List<OrderHistoryDTO> orderHistory = Orders.stream()
        .map(this::convertToOrderHistoryDTO)
        .collect(Collectors.toList());
    
		return new CustomerOrderHistoryDTO(
			customerId,
			customer.getName(),
			customer.getPhoneNum(),
			orderHistory,
			totalSpent,
			Orders.size()
		);
	}

	private OrderHistoryDTO convertToOrderHistoryDTO(Order Order) {
		List<OrderItemSummaryDTO> itemSummary = Order.getItems().stream()
				.map(item -> {
					Product product = item.getProduct();
					OrderItemSummaryDTO dto = new OrderItemSummaryDTO();

					if (product != null) {
						dto.setProductId(product.getId());
						dto.setName(product.getName() != null ? product.getName() : "Unknown");
						dto.setType(product.getType() != null ? product.getType().name() : "Unknown");
						dto.setDetails(product.getDetails() != null ? product.getDetails() : "No details available");
						dto.setPrice(product.getPrice());
					} else {
						dto.setProductId(null);
						dto.setName("Unknown");
						dto.setType("Unknown");
						dto.setDetails("No details available");
						dto.setPrice(BigDecimal.ZERO);
					}

					dto.setQuantity(item.getQty());
					dto.setPrice(item.getPrice());
					dto.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQty())));

					return dto;
				})
				.collect(Collectors.toList());

		return new OrderHistoryDTO(
			Order.getId(),
			Order.getOrderDate(),
			Order.getTotalPrice(),
			itemSummary
		);
	}
}