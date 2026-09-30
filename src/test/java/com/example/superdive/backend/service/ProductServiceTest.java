package com.example.superdive.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.superdive.backend.dto.OrdersItemDTO;
import com.example.superdive.backend.dto.PricedLineDTO;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.enums.ProductType;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private ProductService productService;

    private Product product(Long id, String name, ProductType type, String price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setType(type);
        product.setDetails(name + " details");
        product.setPrice(new BigDecimal(price));
        return product;
    }

    private OrdersItemDTO line(Long productId, Integer qty, String clientPrice) {
        OrdersItemDTO dto = new OrdersItemDTO();
        dto.setProductId(productId);
        dto.setQty(qty);
        if (clientPrice != null) {
            dto.setPrice(new BigDecimal(clientPrice));
        }
        return dto;
    }

    // ---- resolvePrices ----

    @Test
    void resolvePrices_pricesFromTheStoredRowNotTheRequest() throws Exception {
        when(productRepo.findAllById(anyIterable())).thenReturn(Arrays.asList(
                product(5L, "Aqualung BCD", ProductType.Retail, "100.00"),
                product(9L, "Fun Dive Nusa Penida", ProductType.Trip, "250.00")));

        List<PricedLineDTO> priced = productService.resolvePrices(Arrays.asList(
                line(5L, 2, "0.01"), // a hostile client undercutting the real price
                line(9L, 1, "0.01")));

        assertEquals(2, priced.size());
        assertEquals(new BigDecimal("100.00"), priced.get(0).unitPrice());
        assertEquals(2, priced.get(0).qty().intValue());
        assertEquals(new BigDecimal("250.00"), priced.get(1).unitPrice());
    }

    @Test
    void resolvePrices_reportsEveryUnknownIdAtOnce() {
        // Only 5 exists; 41 and 42 do not.
        when(productRepo.findAllById(anyIterable())).thenReturn(Collections.singletonList(
                product(5L, "Aqualung BCD", ProductType.Retail, "100.00")));

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> productService.resolvePrices(Arrays.asList(
                        line(5L, 1, null), line(41L, 1, null), line(42L, 1, null))));

        assertEquals("Unknown product ids: [41, 42]", thrown.getMessage());
    }

    @Test
    void resolvePrices_throwsBeforeQueryingWhenALineHasNoProductId() {
        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> productService.resolvePrices(Collections.singletonList(line(null, 1, null))));

        assertEquals("Product id is required for every order line", thrown.getMessage());
        // A null must never reach findAllById, which rejects it with a
        // data-access exception rather than a readable message.
        verifyNoInteractions(productRepo);
    }

    // ---- reads ----

    @Test
    void getProductById_throwsWhenTheRowIsMissing() {
        when(productRepo.findById(99L)).thenReturn(Optional.empty());

        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> productService.getProductById(99L));

        assertEquals("Product not found with id: 99", thrown.getMessage());
    }

    @Test
    void getProductsByType_throwsOnUnknownType() {
        MessageErrorException thrown = assertThrows(MessageErrorException.class,
                () -> productService.getProductsByType("Submarine"));

        assertEquals("Unknown product type: Submarine", thrown.getMessage());
        verifyNoInteractions(productRepo);
    }

    @Test
    void getProductsByType_mapsRowsToDtos() throws Exception {
        when(productRepo.findByType(ProductType.Retail)).thenReturn(Collections.singletonList(
                product(5L, "Aqualung BCD", ProductType.Retail, "100.00")));

        var products = productService.getProductsByType("Retail");

        assertEquals(1, products.size());
        assertEquals(5L, products.get(0).getId().longValue());
        assertEquals("Aqualung BCD", products.get(0).getName());
        assertEquals(ProductType.Retail, products.get(0).getType());
        assertEquals(new BigDecimal("100.00"), products.get(0).getPrice());
    }
}
