package com.example.superdive.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.superdive.backend.dto.CustomerDTO;
import com.example.superdive.backend.dto.CustomerOrdersHistoryDTO;
import com.example.superdive.backend.dto.OrdersDTO;
import com.example.superdive.backend.dto.OrdersHistoryDTO;
import com.example.superdive.backend.dto.OrdersItemDTO;
import com.example.superdive.backend.dto.OrdersItemSummaryDTO;
import com.example.superdive.backend.dto.ProductDTO;
import com.example.superdive.backend.entity.Customer;
import com.example.superdive.backend.entity.Orders;
import com.example.superdive.backend.entity.OrdersItem;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.entity.User;
import com.example.superdive.backend.enums.PaymentStatus;
import com.example.superdive.backend.enums.ProductType;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.repository.OrdersRepository;
import com.example.superdive.backend.repository.ProductRepository;
import com.example.superdive.backend.security.AuthenticatedUserProvider;
import com.example.superdive.catalog.v1.Money;
import com.example.superdive.catalog.v1.PricedLine;
import com.example.superdive.catalog.v1.ResolvePricesResponse;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    // Field names match the @Autowired fields in OrdersService so Mockito can
    // inject them by name when the types alone are not enough.
    @Mock
    private CustomerService customerService;

    @Mock
    private ProductService prodService;

    @Mock
    private OrdersRepository ordersRepo;

    @Mock
    private ProductRepository productRepo;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private OrdersService ordersService;

    private final AtomicLong productIdSequence = new AtomicLong(100);

    /**
     * Stands in for the catalog service's product table. Order lines now name a
     * product by id instead of carrying a whole product to create, so the
     * fixtures need somewhere for those ids to resolve to.
     */
    private final Map<Long, ProductDTO> catalog = new LinkedHashMap<>();

    // ---- fixtures ----

    private OrdersItemDTO itemDTO(String name, ProductType type, String price, Integer qty) {
        long id = productIdSequence.incrementAndGet();

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(id);
        productDTO.setName(name);
        productDTO.setType(type);
        productDTO.setDetails(name + " details");
        productDTO.setPrice(new BigDecimal(price));
        catalog.put(id, productDTO);

        OrdersItemDTO dto = new OrdersItemDTO();
        dto.setProductId(id);
        dto.setQty(qty);
        // Still populated so the tests prove it is IGNORED: the price used is
        // the one catalog returns, never the one the client sent.
        dto.setPrice(new BigDecimal(price));
        return dto;
    }

    private OrdersDTO ordersDTO(OrdersItemDTO... items) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("John Doe");
        customerDTO.setPhoneNum("1234567890");

        OrdersDTO dto = new OrdersDTO();
        dto.setCustomer(customerDTO);
        dto.setordersItems(new ArrayList<>(Arrays.asList(items)));
        return dto;
    }

    private Customer customer(Long id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("John Doe");
        customer.setPhoneNum("1234567890");
        return customer;
    }

    private Product product(Long id, String name, ProductType type, String price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setType(type);
        product.setDetails(name + " details");
        product.setPrice(new BigDecimal(price));
        return product;
    }

    /** The catalog view of a product entity, as ProductService now returns it. */
    private ProductDTO dtoOf(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setType(product.getType());
        dto.setDetails(product.getDetails());
        dto.setPrice(product.getPrice());
        return dto;
    }

    private OrdersItem orderItem(Product product, Integer qty, String price) {
        OrdersItem item = new OrdersItem();
        item.setProduct(product);
        item.setQty(qty);
        item.setPrice(new BigDecimal(price));
        return item;
    }

    /** An order as it would come back from the repository: id already assigned. */
    private Orders persistedOrders(Long id, OrdersItem... items) {
        Orders orders = new Orders();
        orders.setId(id);
        for (OrdersItem item : items) {
            orders.addItem(item);
        }
        orders.calculateTotal();
        return orders;
    }

    private Money money(BigDecimal amount) {
        BigDecimal scaled = amount.setScale(9, java.math.RoundingMode.HALF_UP);
        long units = scaled.longValue();
        int nanos = scaled.subtract(BigDecimal.valueOf(units)).movePointRight(9).intValueExact();
        return Money.newBuilder().setCurrencyCode("IDR").setUnits(units).setNanos(nanos).build();
    }

    /**
     * Catalog prices the basket. Replaces the old stubProductCreation(): orders
     * no longer create a product per line, they resolve existing ones by id.
     */
    private void stubCatalogResolve() throws MessageErrorException {
        when(prodService.resolvePrices(anyList())).thenAnswer(invocation -> {
            List<OrdersItemDTO> lines = invocation.getArgument(0);
            ResolvePricesResponse.Builder response = ResolvePricesResponse.newBuilder();
            BigDecimal total = BigDecimal.ZERO;

            for (OrdersItemDTO line : lines) {
                ProductDTO p = catalog.get(line.getProductId());
                if (p == null) {
                    response.addMissingIds(line.getProductId());
                    continue;
                }
                BigDecimal subtotal = p.getPrice().multiply(BigDecimal.valueOf(line.getQty()));
                total = total.add(subtotal);
                response.addLines(PricedLine.newBuilder()
                        .setProductId(line.getProductId())
                        .setName(p.getName())
                        .setQty(line.getQty())
                        .setUnitPrice(money(p.getPrice()))
                        .setSubtotal(money(subtotal))
                        .build());
            }
            return response.setTotal(money(total)).build();
        });
    }

    /** The lazy FK proxy OrdersService attaches to each line. */
    private void stubProductReferences() {
        when(productRepo.getReferenceById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            ProductDTO p = catalog.get(id);
            return product(id, p.getName(), p.getType(), p.getPrice().toPlainString());
        });
    }

    private void stubOrdersSaveEchoesArgument() {
        when(ordersRepo.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ---- createOrders ----

    @Test
    void createOrders_mapsItemsAndCalculatesTotal() throws Exception {
        OrdersDTO request = ordersDTO(
                itemDTO("Aqualung BCD", ProductType.Retail, "100.00", 2),
                itemDTO("Fun Dive Nusa Penida", ProductType.Trip, "50.50", 1));

        Customer customer = customer(1L);
        User currentUser = new User();

        when(customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890")).thenReturn(customer);
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(currentUser);
        stubCatalogResolve();
        stubProductReferences();
        stubOrdersSaveEchoesArgument();

        Orders saved = ordersService.createOrders(request);

        assertSame(customer, saved.getCustomer());
        assertSame(currentUser, saved.getUser());
        assertNotNull(saved.getordersDate());
        assertEquals(2, saved.getItems().size());
        // 100.00 x 2 + 50.50 x 1
        assertEquals(new BigDecimal("250.50"), saved.getTotalPrice());
        // every item must point back at the order, otherwise the cascade insert fails
        assertSame(saved, saved.getItems().get(0).getorders());
        assertSame(saved, saved.getItems().get(1).getorders());
        assertEquals("Aqualung BCD", saved.getItems().get(0).getProduct().getName());
    }

    @Test
    void createOrders_defaultsToUnpaidWhenStatusOmitted() throws Exception {
        OrdersDTO request = ordersDTO(itemDTO("Aqualung BCD", ProductType.Retail, "100.00", 1));

        when(customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890")).thenReturn(customer(1L));
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());
        stubCatalogResolve();
        stubProductReferences();
        stubOrdersSaveEchoesArgument();

        Orders saved = ordersService.createOrders(request);

        assertEquals(PaymentStatus.UNPAID, saved.getPaymentStatus());
    }

    @Test
    void createOrders_acceptsPaymentStatusLabelFromTheUi() throws Exception {
        OrdersDTO request = ordersDTO(itemDTO("Aqualung BCD", ProductType.Retail, "100.00", 1));
        request.setPaymentStatus("Paid"); // the label the UI shows, not the enum name

        when(customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890")).thenReturn(customer(1L));
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());
        stubCatalogResolve();
        stubProductReferences();
        stubOrdersSaveEchoesArgument();

        Orders saved = ordersService.createOrders(request);

        assertEquals(PaymentStatus.PAID, saved.getPaymentStatus());
    }

    @Test
    void createOrders_throwsWhenItemsEmpty() {
        OrdersDTO request = ordersDTO(); // no items at all

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> ordersService.createOrders(request));

        assertEquals("orders must contain at least one item.", thrown.getMessage());
        verifyNoInteractions(customerService, prodService, ordersRepo, authenticatedUserProvider);
    }

    @Test
    void createOrders_throwsWhenItemsNull() {
        OrdersDTO request = ordersDTO();
        request.setordersItems(null);

        assertThrows(MessageErrorException.class, () -> ordersService.createOrders(request));

        verifyNoInteractions(ordersRepo);
    }

    @Test
    void createOrders_throwsOnUnknownPaymentStatus() throws Exception {
        OrdersDTO request = ordersDTO(itemDTO("Aqualung BCD", ProductType.Retail, "100.00", 1));
        request.setPaymentStatus("Cancelled");

        // the customer lookup happens before the status is parsed
        when(customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890")).thenReturn(customer(1L));

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> ordersService.createOrders(request));

        assertEquals("Unknown payment status: Cancelled", thrown.getMessage());
        verify(ordersRepo, never()).save(any(Orders.class));
    }

    @Test
    void createOrders_throwsWhenQtyIsNotPositive() throws Exception {
        OrdersDTO request = ordersDTO(itemDTO("Aqualung BCD", ProductType.Retail, "100.00", 0));

        when(customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890")).thenReturn(customer(1L));
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> ordersService.createOrders(request));

        assertEquals("Invalid quantity for product", thrown.getMessage());
        verify(prodService, never()).createProduct(any(ProductDTO.class));
        verify(ordersRepo, never()).save(any(Orders.class));
    }

    @Test
    void createOrders_throwsWhenQtyIsNull() throws Exception {
        OrdersDTO request = ordersDTO(itemDTO("Aqualung BCD", ProductType.Retail, "100.00", null));

        when(customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890")).thenReturn(customer(1L));
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());

        assertThrows(MessageErrorException.class, () -> ordersService.createOrders(request));

        verify(ordersRepo, never()).save(any(Orders.class));
    }

    @Test
    void createOrders_propagatesUnknownCustomer() throws Exception {
        OrdersDTO request = ordersDTO(itemDTO("Aqualung BCD", ProductType.Retail, "100.00", 1));

        when(customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890"))
                .thenThrow(new MessageErrorException("John Doe with phone number 1234567890 not found"));

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> ordersService.createOrders(request));

        assertEquals("John Doe with phone number 1234567890 not found", thrown.getMessage());
        verify(ordersRepo, never()).save(any(Orders.class));
    }

    @Test
    void createOrders_savesTheOrderItBuilt() throws Exception {
        OrdersDTO request = ordersDTO(itemDTO("Aqualung BCD", ProductType.Retail, "100.00", 1));

        when(customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890")).thenReturn(customer(1L));
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());
        stubCatalogResolve();
        stubProductReferences();
        stubOrdersSaveEchoesArgument();

        Orders returned = ordersService.createOrders(request);

        ArgumentCaptor<Orders> captor = ArgumentCaptor.forClass(Orders.class);
        verify(ordersRepo).save(captor.capture());
        assertSame(returned, captor.getValue());
    }

    @Test
    void createOrders_referencesExistingProductsInsteadOfCreatingThem() throws Exception {
        OrdersDTO request = ordersDTO(itemDTO("Aqualung BCD", ProductType.Retail, "100.00", 2));

        when(customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890"))
                .thenReturn(customer(1L));
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());
        stubCatalogResolve();
        stubProductReferences();
        stubOrdersSaveEchoesArgument();

        ordersService.createOrders(request);

        // The regression this guards: createOrders used to INSERT a fresh
        // product row for every order line.
        verify(prodService, never()).createProduct(any(ProductDTO.class));
    }

    @Test
    void createOrders_pricesFromCatalogAndIgnoresClientSuppliedPrice() throws Exception {
        OrdersItemDTO tampered = itemDTO("Aqualung BCD", ProductType.Retail, "100.00", 2);
        tampered.setPrice(new BigDecimal("0.01")); // what a hostile client might send

        when(customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890"))
                .thenReturn(customer(1L));
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());
        stubCatalogResolve();
        stubProductReferences();
        stubOrdersSaveEchoesArgument();

        Orders saved = ordersService.createOrders(ordersDTO(tampered));

        // Catalog's 100.00, not the 0.01 the client asked for.
        assertEquals(new BigDecimal("100.00"), saved.getItems().get(0).getPrice());
        assertEquals(new BigDecimal("200.00"), saved.getTotalPrice());
    }

    @Test
    void createOrders_throwsWhenCatalogDoesNotKnowTheProduct() throws Exception {
        OrdersItemDTO unknown = new OrdersItemDTO();
        unknown.setProductId(4242L); // never registered in the catalog fixture
        unknown.setQty(1);

        when(customerService.findCustomerByNameAndPhoneNum("John Doe", "1234567890"))
                .thenReturn(customer(1L));
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(new User());
        stubCatalogResolve();

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> ordersService.createOrders(ordersDTO(unknown)));

        assertEquals("Unknown product ids: [4242]", thrown.getMessage());
        verify(ordersRepo, never()).save(any(Orders.class));
    }

    // ---- addProductToOrders ----

    @Test
    void addProductToOrders_appendsItemWhenProductIsNew() throws Exception {
        Product bcd = product(5L, "Aqualung BCD", ProductType.Retail, "100.00");
        Product trip = product(9L, "Fun Dive Nusa Penida", ProductType.Trip, "250.00");
        Orders existing = persistedOrders(10L, orderItem(bcd, 2, "100.00"));

        OrdersItemDTO itemDTO = new OrdersItemDTO();
        itemDTO.setProductId(9L);
        itemDTO.setQty(1);
        itemDTO.setPrice(new BigDecimal("250.00"));

        when(ordersRepo.findByIdWithItemsAndProducts(10L)).thenReturn(Optional.of(existing));
        // Catalog confirms the product and supplies the price.
        when(prodService.getProductById(9L)).thenReturn(dtoOf(trip));
        when(productRepo.getReferenceById(9L)).thenReturn(trip);
        stubOrdersSaveEchoesArgument();

        Orders saved = ordersService.addProductToOrders(10L, itemDTO);

        assertEquals(2, saved.getItems().size());
        // 100.00 x 2 + 250.00 x 1
        assertEquals(new BigDecimal("450.00"), saved.getTotalPrice());
        assertSame(saved, saved.getItems().get(1).getorders());
    }

    @Test
    void addProductToOrders_incrementsQtyWhenProductAlreadyInOrders() throws Exception {
        Product bcd = product(5L, "Aqualung BCD", ProductType.Retail, "100.00");
        Orders existing = persistedOrders(10L, orderItem(bcd, 2, "100.00"));

        OrdersItemDTO itemDTO = new OrdersItemDTO();
        itemDTO.setProductId(5L);
        itemDTO.setQty(3);
        itemDTO.setPrice(new BigDecimal("999.99")); // ignored: the existing line keeps its price

        when(ordersRepo.findByIdWithItemsAndProducts(10L)).thenReturn(Optional.of(existing));
        when(prodService.getProductById(5L)).thenReturn(dtoOf(bcd));
        stubOrdersSaveEchoesArgument();

        Orders saved = ordersService.addProductToOrders(10L, itemDTO);

        assertEquals(1, saved.getItems().size());
        assertEquals(5, saved.getItems().get(0).getQty().intValue());
        assertEquals(new BigDecimal("100.00"), saved.getItems().get(0).getPrice());
        assertEquals(new BigDecimal("500.00"), saved.getTotalPrice());
    }

    @Test
    void addProductToOrders_throwsWhenOrdersNotFound() throws Exception {
        when(ordersRepo.findByIdWithItemsAndProducts(99L)).thenReturn(Optional.empty());

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> ordersService.addProductToOrders(99L, new OrdersItemDTO()));

        assertEquals("orders not found with id: 99", thrown.getMessage());
        verify(prodService, never()).getProductById(anyLong());
        verify(ordersRepo, never()).save(any(Orders.class));
    }

    // ---- getOrdersById ----

    @Test
    void getOrdersById_returnsOrders() throws Exception {
        Orders orders = persistedOrders(10L);
        when(ordersRepo.findByIdWithItemsAndProducts(10L)).thenReturn(Optional.of(orders));

        assertSame(orders, ordersService.getOrdersById(10L));
    }

    @Test
    void getOrdersById_throwsWhenNotFound() {
        when(ordersRepo.findByIdWithItemsAndProducts(99L)).thenReturn(Optional.empty());

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> ordersService.getOrdersById(99L));

        assertEquals("orders not found with id: 99", thrown.getMessage());
    }

    // ---- updatePaymentStatus ----

    @Test
    void updatePaymentStatus_acceptsEnumName() throws Exception {
        Orders orders = persistedOrders(10L);
        when(ordersRepo.findByIdWithItemsAndProducts(10L)).thenReturn(Optional.of(orders));
        stubOrdersSaveEchoesArgument();

        Orders saved = ordersService.updatePaymentStatus(10L, "PAID");

        assertEquals(PaymentStatus.PAID, saved.getPaymentStatus());
        verify(ordersRepo).save(orders);
    }

    @Test
    void updatePaymentStatus_acceptsUiLabel() throws Exception {
        Orders orders = persistedOrders(10L);
        when(ordersRepo.findByIdWithItemsAndProducts(10L)).thenReturn(Optional.of(orders));
        stubOrdersSaveEchoesArgument();

        Orders saved = ordersService.updatePaymentStatus(10L, "Partial");

        assertEquals(PaymentStatus.PARTIAL, saved.getPaymentStatus());
    }

    @Test
    void updatePaymentStatus_throwsOnUnknownStatusBeforeTouchingTheRepository() {
        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> ordersService.updatePaymentStatus(10L, "Cancelled"));

        assertEquals("Unknown payment status: Cancelled", thrown.getMessage());
        verifyNoInteractions(ordersRepo);
    }

    @Test
    void updatePaymentStatus_throwsWhenOrdersNotFound() {
        when(ordersRepo.findByIdWithItemsAndProducts(99L)).thenReturn(Optional.empty());

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> ordersService.updatePaymentStatus(99L, "PAID"));

        assertEquals("orders not found with id: 99", thrown.getMessage());
        verify(ordersRepo, never()).save(any(Orders.class));
    }

    // ---- read-through methods ----

    @Test
    void getAllOrders_delegatesToRepository() {
        List<Orders> orders = Arrays.asList(persistedOrders(10L), persistedOrders(11L));
        when(ordersRepo.findAll()).thenReturn(orders);

        assertEquals(orders, ordersService.getAllOrders());
    }

    @Test
    void getOrdersByCustomerId_delegatesToRepository() {
        List<Orders> orders = Collections.singletonList(persistedOrders(10L));
        when(ordersRepo.findByCustomerId(1L)).thenReturn(orders);

        assertEquals(orders, ordersService.getOrdersByCustomerId(1L));
    }

    // ---- getCustomerOrdersHistoryDTO ----

    @Test
    void getCustomerOrdersHistoryDTO_summarisesEveryOrders() throws Exception {
        Product bcd = product(5L, "Aqualung BCD", ProductType.Retail, "100.00");

        Orders first = persistedOrders(10L, orderItem(bcd, 2, "100.00")); // 200.00
        first.setordersDate(LocalDateTime.of(2026, 1, 15, 10, 0));
        Orders second = persistedOrders(11L, orderItem(bcd, 1, "150.00")); // 150.00
        second.setordersDate(LocalDateTime.of(2026, 2, 20, 9, 30));

        when(customerService.getCustomerById(1L)).thenReturn(customer(1L));
        when(ordersRepo.findByCustomerIdWithItemsAndProducts(1L)).thenReturn(Arrays.asList(first, second));

        CustomerOrdersHistoryDTO history = ordersService.getCustomerOrdersHistoryDTO(1L);

        assertEquals(1L, history.getCustomerId().longValue());
        assertEquals("John Doe", history.getCustomerName());
        assertEquals("1234567890", history.getPhoneNum());
        assertEquals(new BigDecimal("350.00"), history.getTotalPrice());
        // totalItems is really the order count, not the item count
        assertEquals(2, history.getTotalItems().intValue());

        assertEquals(2, history.getOrders().size());
        OrdersHistoryDTO firstHistory = history.getOrders().get(0);
        assertEquals(10L, firstHistory.getOrdersId().longValue());
        assertEquals(LocalDateTime.of(2026, 1, 15, 10, 0), firstHistory.getOrdersDate());
        assertEquals(new BigDecimal("200.00"), firstHistory.getTotalAmount());

        OrdersItemSummaryDTO summary = firstHistory.getItems().get(0);
        assertEquals(5L, summary.getProductId().longValue());
        assertEquals("Aqualung BCD", summary.getName());
        assertEquals("Retail", summary.getType());
        assertEquals("Aqualung BCD details", summary.getDetails());
        assertEquals(2, summary.getQuantity().intValue());
        // price comes from the order line, not the product catalogue
        assertEquals(new BigDecimal("100.00"), summary.getPrice());
        assertEquals(new BigDecimal("200.00"), summary.getSubtotal());
    }

    @Test
    void getCustomerOrdersHistoryDTO_fallsBackWhenProductIsMissing() throws Exception {
        Orders orders = persistedOrders(10L, orderItem(null, 3, "20.00"));

        when(customerService.getCustomerById(1L)).thenReturn(customer(1L));
        when(ordersRepo.findByCustomerIdWithItemsAndProducts(1L))
                .thenReturn(Collections.singletonList(orders));

        CustomerOrdersHistoryDTO history = ordersService.getCustomerOrdersHistoryDTO(1L);

        OrdersItemSummaryDTO summary = history.getOrders().get(0).getItems().get(0);
        assertNull(summary.getProductId());
        assertEquals("Unknown", summary.getName());
        assertEquals("Unknown", summary.getType());
        assertEquals("No details available", summary.getDetails());
        assertEquals(new BigDecimal("60.00"), summary.getSubtotal());
    }

    @Test
    void getCustomerOrdersHistoryDTO_throwsWhenCustomerHasNoOrders() {
        when(customerService.getCustomerById(1L)).thenReturn(customer(1L));
        when(ordersRepo.findByCustomerIdWithItemsAndProducts(1L)).thenReturn(Collections.emptyList());

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> ordersService.getCustomerOrdersHistoryDTO(1L));

        assertEquals("No orderss found for this customer", thrown.getMessage());
    }

    @Test
    void getCustomerOrdersHistoryDTO_propagatesUnknownCustomer() {
        when(customerService.getCustomerById(99L)).thenThrow(new RuntimeException("Customer not found"));

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> ordersService.getCustomerOrdersHistoryDTO(99L));

        assertEquals("Customer not found", thrown.getMessage());
        verifyNoInteractions(ordersRepo);
    }
}
