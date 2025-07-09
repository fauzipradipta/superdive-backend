package com.example.superdive.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.dto.OrderItemDTO;
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
	public Sale createOrder(SaleDTO saleDTO) throws MessageErrorException  {

		Customer customer = customerService.findCustomerByNameAndPhoneNum(
			saleDTO.getCustomer().getName(),
			saleDTO.getCustomer().getPhoneNum()
		);
		
		Sale sale = new Sale();
		sale.setCustomer(customer);
		sale.setOrderDate(LocalDateTime.now());

		for (OrderItemDTO itemDTO : saleDTO.getOrderItems()) {

			Product product   = prodService.getProductById(itemDTO.getProductId());
            
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQty(itemDTO.getQty());
            
            sale.addItem(item);
        }
        
        // Calculate total price
        sale.calculateTotal();
        
        return saleRepo.save(sale);
	}

	@Transactional
	public Sale addProductToOrder(Long saleId, OrderItemDTO itemDTO) throws MessageErrorException {
		
		Sale sale = saleRepo.findById(saleId)
			.orElseThrow(() -> new MessageErrorException("Sale not found with id: " + saleId));
		
		Product product = prodService.getProductById(itemDTO.getProductId()); 

		Optional<OrderItem> existingItem = sale.getItems().stream()
			.filter(item -> item.getProduct().getId().equals(product.getId()))
			.findFirst();
		
		if(existingItem.isPresent()){
			OrderItem item = existingItem.get();
			item.setQty(item.getQty() + itemDTO.getQty());
		}
		else{
			OrderItem item = new OrderItem();
			item.setProduct(product);
			item.setQty(itemDTO.getQty());
			sale.addItem(item);
		}

		sale.calculateTotal();
		return saleRepo.save(sale);
	}

	public Sale getSaleById(Long id) throws MessageErrorException {
		return saleRepo.findById(id)
			.orElseThrow(() -> new MessageErrorException("Order not found" ));
	}

	public List<Sale> getAllSales() {
		return saleRepo.findAll();
	}

}
