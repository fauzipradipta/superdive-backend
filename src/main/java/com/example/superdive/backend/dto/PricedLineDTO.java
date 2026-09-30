package com.example.superdive.backend.dto;

import java.math.BigDecimal;

/**
 * One order line after the server has priced it.
 *
 * The unit price here always comes from the `product` table, never from the
 * request body, which is what stops an order being booked at a price the
 * client picked. See ProductService#resolvePrices.
 */
public record PricedLineDTO(Long productId, Integer qty, BigDecimal unitPrice) {
}
