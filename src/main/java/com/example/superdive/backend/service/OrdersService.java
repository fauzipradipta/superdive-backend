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
import com.example.superdive.backend.dto.ProductDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.entity.OrdersItem;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.entity.Orders;
import com.example.superdive.backend.enums.PaymentStatus;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.repository.OrdersRepository;
import com.example.superdive.backend.repository.ProductRepository;
import com.example.superdive.backend.security.AuthenticatedUserProvider;
import com.example.superdive.catalog.v1.PricedLine;
import com.example.superdive.catalog.v1.ResolvePricesResponse;

import jakarta.transaction.Transactional;

@Service
public class OrdersService {

	@Autowired
	private CustomerService customerService;
	@Autowired
	private ProductService prodService;
	@Autowired
	private OrdersRepository ordersRepo;
	/*
	 * Used only to obtain lazy FK references for the OrdersItem association.
	 * Product data itself is read from the catalog service over gRPC; this
	 * repository is deliberately never used to read or write product columns.
	 */
	@Autowired
	private ProductRepository productRepo;
	@Autowired
	private AuthenticatedUserProvider authenticatedUserProvider;

	@Transactional
	public Orders createOrders(OrdersDTO OrdersDTO) throws MessageErrorException {

		if (OrdersDTO.getordersItems() == null || OrdersDTO.getordersItems().isEmpty()) {
			throw new MessageErrorException("orders must contain at least one item.");
		}

		Customer customer = customerService.findCustomerByNameAndPhoneNum(
				OrdersDTO.getCustomer().getName(),
				OrdersDTO.getCustomer().getPhoneNum());

		// Optional on create; omitted or blank leaves the entity's UNPAID default.
		PaymentStatus paymentStatus = PaymentStatus.UNPAID;
		String requestedStatus = OrdersDTO.getPaymentStatus();
		if (requestedStatus != null && !requestedStatus.isBlank()) {
			paymentStatus = PaymentStatus.from(requestedStatus);
			if (paymentStatus == null) {
				throw new MessageErrorException("Unknown payment status: " + requestedStatus);
			}
		}

		Orders orders = new Orders();
		orders.setCustomer(customer);
		orders.setordersDate(LocalDateTime.now());
		orders.setUser(authenticatedUserProvider.getCurrentUser());
		orders.setPaymentStatus(paymentStatus);

		for (OrdersItemDTO itemDTO : OrdersDTO.getordersItems()) {
			if (itemDTO.getQty() == null || itemDTO.getQty() <= 0) {
				throw new MessageErrorException("Invalid quantity for product");
			}
			if (itemDTO.getProductId() == null) {
				throw new MessageErrorException("Product id is required for every order line");
			}
		}

		/*
		 * Resolve every line against the catalog in one call.
		 *
		 * This previously called prodService.createProduct() per line, which
		 * INSERTed a brand-new product row for each item on each order rather
		 * than referencing the product being sold. Prices now come back from
		 * the catalog, so an order cannot be booked at a client-chosen price.
		 */
		ResolvePricesResponse priced = prodService.resolvePrices(OrdersDTO.getordersItems());

		if (!priced.getMissingIdsList().isEmpty()) {
			throw new MessageErrorException("Unknown product ids: " + priced.getMissingIdsList());
		}

		for (PricedLine line : priced.getLinesList()) {
			OrdersItem item = new OrdersItem();
			// getReferenceById yields a lazy FK proxy: it establishes the
			// association without reading product columns back through JPA.
			item.setProduct(productRepo.getReferenceById(line.getProductId()));
			item.setQty(line.getQty());
			item.setPrice(CatalogMapper.toBigDecimal(line.getUnitPrice()));
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

		if (itemDTO.getQty() == null || itemDTO.getQty() <= 0) {
			throw new MessageErrorException("Invalid quantity for product");
		}

		// Confirms the product exists and yields the catalog's own current
		// price. A NOT_FOUND from catalog surfaces as MessageErrorException.
		ProductDTO product = prodService.getProductById(itemDTO.getProductId());

		Optional<OrdersItem> existingItem = orders.getItems().stream()
				.filter(item -> item.getProduct().getId().equals(product.getId()))
				.findFirst();

		if (existingItem.isPresent()) {
			OrdersItem item = existingItem.get();
			item.setQty(item.getQty() + itemDTO.getQty());
		} else {
			OrdersItem item = new OrdersItem();
			item.setProduct(productRepo.getReferenceById(product.getId()));
			item.setQty(itemDTO.getQty());
			item.setPrice(product.getPrice());
			item.setorders(orders);
			orders.addItem(item);

			System.out.println("Item orders reference: " + item.getorders());
			System.out.println("orders items count: " + orders.getItems().size());
		}

		orders.calculateTotal();

		Orders savedOrders = ordersRepo.save(orders);
		System.out.println("Saved orders ID: " + savedOrders.getId());
		System.out.println("Saved orders items count: " + savedOrders.getItems().size());
		return savedOrders;

	}

	/* Fetch-joins items/products/customer: the order preview reads those
	 * collections after the transaction closes. */
	public Orders getOrdersById(Long id) throws MessageErrorException {
		return ordersRepo.findByIdWithItemsAndProducts(id)
				.orElseThrow(() -> new MessageErrorException("orders not found with id: " + id));
	}

	@Transactional
	public Orders updatePaymentStatus(Long id, String paymentStatus) throws MessageErrorException {
		PaymentStatus status = PaymentStatus.from(paymentStatus);
		if (status == null) {
			throw new MessageErrorException("Unknown payment status: " + paymentStatus);
		}

		Orders orders = ordersRepo.findByIdWithItemsAndProducts(id)
				.orElseThrow(() -> new MessageErrorException("orders not found with id: " + id));

		orders.setPaymentStatus(status);
		return ordersRepo.save(orders);
	}

	public List<Orders> getAllOrders() {
		return ordersRepo.findAll();
	}

	public List<Orders> getOrdersByCustomerId(Long customerId) {
		return ordersRepo.findByCustomerId(customerId);
	}

	public CustomerOrdersHistoryDTO getCustomerOrdersHistoryDTO(Long customerId) throws MessageErrorException {

		Customer customer = customerService.getCustomerById(customerId);

		List<Orders> orders = ordersRepo.findByCustomerIdWithItemsAndProducts(customerId);

		if (orders.isEmpty()) {
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
				orders.size());
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
				itemSummary);
	}
}