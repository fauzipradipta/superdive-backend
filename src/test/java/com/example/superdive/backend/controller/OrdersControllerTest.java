package com.example.superdive.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.superdive.backend.dto.CustomerDTO;
import com.example.superdive.backend.dto.CustomerOrdersHistoryDTO;
import com.example.superdive.backend.dto.OrdersDTO;
import com.example.superdive.backend.dto.OrdersHistoryDTO;
import com.example.superdive.backend.dto.OrdersItemDTO;
import com.example.superdive.backend.dto.OrdersItemSummaryDTO;
import com.example.superdive.backend.dto.ProductDTO;
import com.example.superdive.backend.dto.Request.PaymentStatusRequestDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.entity.Orders;
import com.example.superdive.backend.entity.OrdersItem;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.enums.PaymentStatus;
import com.example.superdive.backend.enums.ProductType;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.service.OrdersService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class OrdersControllerTest {

	@Mock
	private OrdersService ordersService;

	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new OrdersController(ordersService)).build();
	}

	// ---- fixtures ----

	private Product product(Long id, String name) {
		Product product = new Product();
		product.setId(id);
		product.setName(name);
		product.setType(ProductType.Retail);
		product.setDetails(name + " details");
		product.setPrice(new BigDecimal("100.00"));
		return product;
	}

	private OrdersItem orderItem(Product product, Integer qty, String price) {
		OrdersItem item = new OrdersItem();
		item.setProduct(product);
		item.setQty(qty);
		item.setPrice(new BigDecimal(price));
		return item;
	}

	/** What the service hands back after a successful save. */
	private Orders savedOrders(Long id, OrdersItem... items) {
		Customer customer = new Customer();
		customer.setId(1L);
		customer.setName("John Doe");
		customer.setPhoneNum("1234567890");

		Orders orders = new Orders();
		orders.setId(id);
		orders.setCustomer(customer);
		orders.setordersDate(LocalDateTime.of(2026, 1, 15, 10, 0));
		orders.setPaymentStatus(PaymentStatus.UNPAID);
		for (OrdersItem item : items) {
			orders.addItem(item);
		}
		orders.calculateTotal();
		return orders;
	}

	private OrdersDTO createOrdersRequest() {
		CustomerDTO customer = new CustomerDTO();
		customer.setName("John Doe");
		customer.setPhoneNum("1234567890");

		ProductDTO productDTO = new ProductDTO();
		productDTO.setName("Aqualung BCD");
		productDTO.setType(ProductType.Retail);
		productDTO.setDetails("Size M, black");
		productDTO.setPrice(new BigDecimal("100.00"));

		OrdersItemDTO item = new OrdersItemDTO();
		item.setProductDTO(productDTO);
		item.setQty(2);
		item.setPrice(new BigDecimal("100.00"));

		OrdersDTO request = new OrdersDTO();
		request.setCustomer(customer);
		request.setordersItems(Collections.singletonList(item));
		return request;
	}

	// ---- POST /api/create-orders ----

	@Test
	void createOrders_returns201AndTheSavedOrders() throws Exception {
		Orders created = savedOrders(10L, orderItem(product(5L, "Aqualung BCD"), 2, "100.00"));

		when(ordersService.createOrders(any(OrdersDTO.class))).thenReturn(created);

		mockMvc.perform(post("/api/create-orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createOrdersRequest())))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.totalPrice").value(200.00))
				.andExpect(jsonPath("$.paymentStatus").value("UNPAID"))
				.andExpect(jsonPath("$.customer.name").value("John Doe"))
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].product.name").value("Aqualung BCD"))
				.andExpect(jsonPath("$.ordersDate").exists());
	}

	@Test
	void createOrders_returns400WithMessage_whenServiceRejectsIt() throws Exception {
		when(ordersService.createOrders(any(OrdersDTO.class)))
				.thenThrow(new MessageErrorException("orders must contain at least one item."));

		mockMvc.perform(post("/api/create-orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createOrdersRequest())))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Error: orders must contain at least one item."));
	}

	// ---- POST /api/{ordersId}/items ----

	@Test
	void addProductToOrders_returns200AndTheUpdatedOrders() throws Exception {
		Orders updated = savedOrders(10L,
				orderItem(product(5L, "Aqualung BCD"), 2, "100.00"),
				orderItem(product(9L, "Regulator"), 1, "250.00"));

		when(ordersService.addProductToOrders(eq(10L), any(OrdersItemDTO.class))).thenReturn(updated);

		OrdersItemDTO body = new OrdersItemDTO();
		body.setProductId(9L);
		body.setQty(1);
		body.setPrice(new BigDecimal("250.00"));

		mockMvc.perform(post("/api/10/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(body)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.totalPrice").value(450.00));
	}

	@Test
	void addProductToOrders_returns400WithEmptyBody_whenOrdersNotFound() throws Exception {
		when(ordersService.addProductToOrders(eq(99L), any(OrdersItemDTO.class)))
				.thenThrow(new MessageErrorException("orders not found with id: 99"));

		mockMvc.perform(post("/api/99/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new OrdersItemDTO())))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(""));
	}

	// ---- GET /api/orders/{id} ----

	@Test
	void getOrders_returns200() throws Exception {
		when(ordersService.getOrdersById(10L))
				.thenReturn(savedOrders(10L, orderItem(product(5L, "Aqualung BCD"), 2, "100.00")));

		mockMvc.perform(get("/api/orders/10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.items[0].qty").value(2))
				.andExpect(jsonPath("$.items[0].price").value(100.00));
	}

	@Test
	void getOrders_returns404_whenOrdersNotFound() throws Exception {
		when(ordersService.getOrdersById(99L))
				.thenThrow(new MessageErrorException("orders not found with id: 99"));

		mockMvc.perform(get("/api/orders/99"))
				.andExpect(status().isNotFound());
	}

	// ---- PATCH /api/orders/{id}/payment-status ----

	@Test
	void updatePaymentStatus_returns200AndPassesTheRawValueToTheService() throws Exception {
		Orders updated = savedOrders(10L, orderItem(product(5L, "Aqualung BCD"), 2, "100.00"));
		updated.setPaymentStatus(PaymentStatus.PAID);

		when(ordersService.updatePaymentStatus(10L, "Paid")).thenReturn(updated);

		mockMvc.perform(patch("/api/orders/10/payment-status")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new PaymentStatusRequestDTO("Paid"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.paymentStatus").value("PAID"));

		verify(ordersService).updatePaymentStatus(10L, "Paid");
	}

	@Test
	void updatePaymentStatus_returns400WithMessage_onUnknownStatus() throws Exception {
		when(ordersService.updatePaymentStatus(10L, "Cancelled"))
				.thenThrow(new MessageErrorException("Unknown payment status: Cancelled"));

		mockMvc.perform(patch("/api/orders/10/payment-status")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new PaymentStatusRequestDTO("Cancelled"))))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Error: Unknown payment status: Cancelled"));
	}

	// ---- GET /api/payment-statuses ----

	@Test
	void getPaymentStatuses_returnsEveryEnumName() throws Exception {
		mockMvc.perform(get("/api/payment-statuses"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(5))
				.andExpect(jsonPath("$[0]").value("UNPAID"))
				.andExpect(jsonPath("$[1]").value("PARTIAL"))
				.andExpect(jsonPath("$[2]").value("PAID"))
				.andExpect(jsonPath("$[3]").value("OVERDUE"))
				.andExpect(jsonPath("$[4]").value("REFUNDED"));

		// the endpoint reads the enum directly, it must not hit the service
		verifyNoInteractions(ordersService);
	}

	// ---- GET /api/all-orders ----

	@Test
	void getAllOrders_returns200WithEveryOrders() throws Exception {
		when(ordersService.getAllOrders()).thenReturn(Arrays.asList(
				savedOrders(10L, orderItem(product(5L, "Aqualung BCD"), 2, "100.00")),
				savedOrders(11L, orderItem(product(9L, "Regulator"), 1, "250.00"))));

		mockMvc.perform(get("/api/all-orders"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value(10))
				.andExpect(jsonPath("$[1].id").value(11));
	}

	@Test
	void getAllOrders_returns200WithEmptyList() throws Exception {
		when(ordersService.getAllOrders()).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/api/all-orders"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));
	}

	// ---- GET /api/customer-orders-history/{customerId} ----

	@Test
	void getCustomerOrdersHistory_returns200() throws Exception {
		OrdersItemSummaryDTO item = new OrdersItemSummaryDTO(5L, "Aqualung BCD", "Retail", "Size M, black",
				2, new BigDecimal("100.00"), new BigDecimal("200.00"));
		OrdersHistoryDTO order = new OrdersHistoryDTO(10L, LocalDateTime.of(2026, 1, 15, 10, 0),
				new BigDecimal("200.00"), Collections.singletonList(item));
		CustomerOrdersHistoryDTO history = new CustomerOrdersHistoryDTO(1L, "John Doe", "1234567890",
				Collections.singletonList(order), new BigDecimal("200.00"), 1);

		when(ordersService.getCustomerOrdersHistoryDTO(1L)).thenReturn(history);

		mockMvc.perform(get("/api/customer-orders-history/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(1))
				.andExpect(jsonPath("$.customerName").value("John Doe"))
				.andExpect(jsonPath("$.totalPrice").value(200.00))
				.andExpect(jsonPath("$.totalItems").value(1))
				.andExpect(jsonPath("$.orders.length()").value(1))
				.andExpect(jsonPath("$.orders[0].ordersId").value(10))
				.andExpect(jsonPath("$.orders[0].items[0].name").value("Aqualung BCD"))
				.andExpect(jsonPath("$.orders[0].items[0].subtotal").value(200.00));
	}

	@Test
	void getCustomerOrdersHistory_returns404WithMessage_whenCustomerHasNoOrders() throws Exception {
		when(ordersService.getCustomerOrdersHistoryDTO(1L))
				.thenThrow(new MessageErrorException("No orderss found for this customer"));

		mockMvc.perform(get("/api/customer-orders-history/1"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("No orderss found for this customer"));
	}
}
