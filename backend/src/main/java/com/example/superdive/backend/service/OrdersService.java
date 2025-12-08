package com.example.superdive.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.dto.CustomerOrdersHistoryDTO;
import com.example.superdive.backend.dto.OrdersHistoryDTO;
import com.example.superdive.backend.dto.OrdersItemDTO;
import com.example.superdive.backend.dto.OrdersItemSummaryDTO;
import com.example.superdive.backend.dto.OrdersDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.entity.OrdersItem;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.entity.Orders;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.repository.OrdersRepository;

import jakarta.transaction.Transactional;

@Service
public class OrdersService {
	
	@Autowired
	private CustomerService customerService; 
	@Autowired 
	private ProductService prodService;
	@Autowired
	private OrdersRepository ordersRepo;

	

	@Transactional
	public Orders createOrders(OrdersDTO OrdersDTO) throws MessageErrorException {
		
		
	    if (OrdersDTO.getordersItems() == null || OrdersDTO.getordersItems().isEmpty()) {
	        throw new MessageErrorException("orders must contain at least one item.");
	    }

	    Customer customer = customerService.findCustomerByNameAndPhoneNum(
	        OrdersDTO.getCustomer().getName(),
	        OrdersDTO.getCustomer().getPhoneNum()
	    );

	    Orders orders = new Orders();
	    orders.setCustomer(customer);
	    orders.setordersDate(LocalDateTime.now());

	    for (OrdersItemDTO itemDTO : OrdersDTO.getordersItems()) {
	        if (itemDTO.getQty() == null || itemDTO.getQty() <= 0) {
	            throw new MessageErrorException("Invalid quantity for product");
	        }

	        Product product = prodService.createProduct(itemDTO.getProductDTO());

	        OrdersItem item = new OrdersItem();
	        item.setProduct(product);
	        item.setQty(itemDTO.getQty());
	        item.setPrice(itemDTO.getPrice());
	        item.setorders(orders); 
	        
	        
	        orders.addItem(item); 
		}

		orders.calculateTotal();
		return ordersRepo.save(orders); 
	}

	@Transactional
	public Orders addProductToOrders(Long ordersId, OrdersItemDTO itemDTO) throws MessageErrorException {
		Orders orders = ordersRepo.findByIdWithItemsAndProducts(ordersId)
				.orElseThrow(() -> new MessageErrorException("orders not found with id: " + ordersId));

		Product product = prodService.getProductById(itemDTO.getProductId());

		Optional<OrdersItem> existingItem = orders.getItems().stream()
				.filter(item -> item.getProduct().getId().equals(product.getId()))
				.findFirst();

		if (existingItem.isPresent()) {
			OrdersItem item = existingItem.get();
			item.setQty(item.getQty() + itemDTO.getQty());
		} else {
			OrdersItem item = new OrdersItem();
			item.setProduct(product);
			item.setQty(itemDTO.getQty());
			item.setPrice(itemDTO.getPrice()); 
			item.setorders(orders);
			orders.addItem(item);

			System.out.println("Item orders reference: " + item.getorders());
			System.out.println("orders items count: " + orders.getItems().size());
		}

		orders.calculateTotal();

		Orders savedorders = ordersRepo.save(orders);
		System.out.println("Saved orders ID: " + savedorders.getId());
		System.out.println("Saved orders items count: " + savedorders.getItems().size());
		return savedorders;

	}

	public Orders getOrdersById(Long id) throws MessageErrorException {
		return ordersRepo.findById(id)
			.orElseThrow(() -> new MessageErrorException("orders not found" ));
	}

	public List<Orders> getAllOrders() {
		return ordersRepo.findAll();
	}

	public List<Orders> getOrdersByCustomerId(Long customerId) {
		return ordersRepo.findByCustomerId(customerId);
	}

	public CustomerOrdersHistoryDTO getCustomerOrdersHistoryDTO(Long customerId) throws MessageErrorException{

		
		Customer customer = customerService.getCustomerById(customerId);

		
		List<Orders> orders = ordersRepo.findByCustomerIdWithItemsAndProducts(customerId);

		if(orders.isEmpty()){
			throw new MessageErrorException("No orderss found for this customer");
		}

		
		BigDecimal totalSpent = orders.stream()
			.map(Orders::getTotalPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		
		List<OrdersHistoryDTO> ordersHistory = orders.stream()
        .map(this::convertToOrdersHistoryDTO)
        .collect(Collectors.toList());
    
		return new CustomerOrdersHistoryDTO(
			customerId,
			customer.getName(),
			customer.getPhoneNum(),
			ordersHistory,
			totalSpent,
			orders.size()
		);
	}

	private OrdersHistoryDTO convertToOrdersHistoryDTO(Orders orders) {
		List<OrdersItemSummaryDTO> itemSummary = orders.getItems().stream()
				.map(item -> {
					Product product = item.getProduct();
					OrdersItemSummaryDTO dto = new OrdersItemSummaryDTO();

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

		return new OrdersHistoryDTO(
			orders.getId(),
			orders.getordersDate(),
			orders.getTotalPrice(),
			itemSummary
		);
	}
}