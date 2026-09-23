package com.example.superdive.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.example.superdive.backend.dto.ProductDTO;
import com.example.superdive.backend.enums.ProductType;
import com.example.superdive.catalog.v1.Money;

/**
 * Translates catalog wire messages into the DTOs the REST layer already returns,
 * so moving products behind gRPC does not change the shape of the HTTP API.
 */
final class CatalogMapper {

	private static final int NANO_SCALE = 9;
	private static final String PROTO_ENUM_PREFIX = "PRODUCT_TYPE_";

	private CatalogMapper() {
	}

	static BigDecimal toBigDecimal(Money money) {
		if (money == null) {
			return BigDecimal.ZERO;
		}
		BigDecimal exact = BigDecimal.valueOf(money.getUnits())
				.add(BigDecimal.valueOf(money.getNanos(), NANO_SCALE));

		/*
		 * Normalise the scale. The exact construction above always has scale 9,
		 * so a price of 100.00 would come back as 100.000000000 -- which changes
		 * the JSON the frontend renders and, because BigDecimal.equals compares
		 * scale, silently breaks equality against the same amount. Currency is
		 * conventionally 2dp; keep more only when genuinely present.
		 */
		BigDecimal trimmed = exact.stripTrailingZeros();
		return trimmed.scale() < 2 ? trimmed.setScale(2) : trimmed;
	}

	static Money toMoney(BigDecimal amount, String currencyCode) {
		if (amount == null) {
			return Money.newBuilder().setCurrencyCode(currencyCode).build();
		}
		BigDecimal scaled = amount.setScale(NANO_SCALE, RoundingMode.HALF_UP);
		long units = scaled.longValue();
		int nanos = scaled.subtract(BigDecimal.valueOf(units))
				.movePointRight(NANO_SCALE)
				.intValueExact();
		return Money.newBuilder()
				.setCurrencyCode(currencyCode)
				.setUnits(units)
				.setNanos(nanos)
				.build();
	}

	/** BY NAME. See the warning in catalog.proto -- the numbers do not line up. */
	static ProductType toDomainType(com.example.superdive.catalog.v1.ProductType proto) {
		if (proto == null
				|| proto == com.example.superdive.catalog.v1.ProductType.PRODUCT_TYPE_UNSPECIFIED
				|| proto == com.example.superdive.catalog.v1.ProductType.UNRECOGNIZED) {
			return null;
		}
		String bare = proto.name().substring(PROTO_ENUM_PREFIX.length());
		for (ProductType candidate : ProductType.values()) {
			if (candidate.name().equalsIgnoreCase(bare)) {
				return candidate;
			}
		}
		throw new IllegalArgumentException("No ProductType matching " + proto.name());
	}

	static com.example.superdive.catalog.v1.ProductType toProtoType(ProductType domain) {
		if (domain == null) {
			return com.example.superdive.catalog.v1.ProductType.PRODUCT_TYPE_UNSPECIFIED;
		}
		return com.example.superdive.catalog.v1.ProductType.valueOf(
				PROTO_ENUM_PREFIX + domain.name().toUpperCase());
	}

	static ProductDTO toDto(com.example.superdive.catalog.v1.Product proto) {
		ProductDTO dto = new ProductDTO();
		dto.setId(proto.getId());
		dto.setName(proto.getName());
		dto.setType(toDomainType(proto.getType()));
		dto.setDetails(proto.getDetails());
		dto.setPrice(toBigDecimal(proto.getPrice()));
		return dto;
	}
}
