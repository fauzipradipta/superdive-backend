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
import com.example.superdive.backend.dto.SaleDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.entity.OrderItem;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.entity.Sale;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.repository.SaleRepository;

import jakarta.transaction.Transactional;

import com.example.superdive.backend.repository.OrderItemRepository;
@Service
public class SaleService {
	
	@Autowired
	private CustomerService customerService; 
	@Autowired 
	private ProductService prodService;
	@Autowired
	private SaleRepository saleRepo;
	@Autowired
	private OrderItemRepository orderItemRepo;

	

	@Transactional
	public Sale createOrder(SaleDTO saleDTO) throws MessageErrorException {
		
		
	    if (saleDTO.getOrderItems() == null || saleDTO.getOrderItems().isEmpty()) {
	        throw new MessageErrorException("Order must contain at least one item.");
	    }

	    Customer customer = customerService.findCustomerByNameAndPhoneNum(
	        saleDTO.getCustomer().getName(),
	        saleDTO.getCustomer().getPhoneNum()
	    );

	    Sale sale = new Sale();
	    sale.setCustomer(customer);
	    sale.setOrderDate(LocalDateTime.now());

	    for (OrderItemDTO itemDTO : saleDTO.getOrderItems()) {
	        if (itemDTO.getQty() == null || itemDTO.getQty() <= 0) {
	            throw new MessageErrorException("Invalid quantity for product");
	        }

	        Product product = prodService.createProduct(itemDTO.getProductDTO());

	        OrderItem item = new OrderItem();
	        item.setProduct(product);
	        item.setQty(itemDTO.getQty());
	        item.setPrice(itemDTO.getPrice());
	        item.setSale(sale); 
	        
	        
	        sale.addItem(item); 
		}

		sale.calculateTotal();
		return saleRepo.save(sale); 
	}

	@Transactional
	public Sale addProductToOrder(Long saleId, OrderItemDTO itemDTO) throws MessageErrorException {
		Sale sale = saleRepo.findByIdWithItemsAndProducts(saleId)
				.orElseThrow(() -> new MessageErrorException("Sale not found with id: " + saleId));

		Product product = prodService.getProductById(itemDTO.getProductId());

		Optional<OrderItem> existingItem = sale.getItems().stream()
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
			item.setSale(sale);
			sale.addItem(item);

			System.out.println("Item sale reference: " + item.getSale());
			System.out.println("Sale items count: " + sale.getItems().size());
		}

		sale.calculateTotal();

		Sale savedSale = saleRepo.save(sale);
		System.out.println("Saved sale ID: " + savedSale.getId());
		System.out.println("Saved sale items count: " + savedSale.getItems().size());
		return savedSale;

	}

	public Sale getSaleById(Long id) throws MessageErrorException {
		return saleRepo.findById(id)
			.orElseThrow(() -> new MessageErrorException("Order not found" ));
	}

	public List<Sale> getAllSales() {
		return saleRepo.findAll();
	}

	public List<Sale> getSalesByCustomerId(Long customerId) {
		return saleRepo.findByCustomerId(customerId);
	}

	public CustomerOrderHistoryDTO getCustomerOrderHistoryDTO(Long customerId) throws MessageErrorException{

		
		Customer customer = customerService.getCustomerById(customerId);

		
		List<Sale> sales = saleRepo.findByCustomerIdWithItemsAndProducts(customerId);

		if(sales.isEmpty()){
			throw new MessageErrorException("No orders found for this customer");
		}

		
		BigDecimal totalSpent = sales.stream()
			.map(Sale::getTotalPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		
		List<OrderHistoryDTO> orderHistory = sales.stream()
        .map(this::convertToOrderHistoryDTO)
        .collect(Collectors.toList());
    
		return new CustomerOrderHistoryDTO(
			customerId,
			customer.getName(),
			customer.getPhoneNum(),
			orderHistory,
			totalSpent,
			sales.size()
		);
	}

	private OrderHistoryDTO convertToOrderHistoryDTO(Sale sale) {
		List<OrderItemSummaryDTO> itemSummary = sale.getItems().stream()
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
			sale.getId(),
			sale.getOrderDate(),
			sale.getTotalPrice(),
			itemSummary
		);
	}
}