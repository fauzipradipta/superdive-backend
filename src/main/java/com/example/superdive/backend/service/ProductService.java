package com.example.superdive.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.dto.OrdersItemDTO;
import com.example.superdive.backend.dto.ProductDTO;
import com.example.superdive.backend.enums.ProductType;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.catalog.v1.CatalogServiceGrpc;
import com.example.superdive.catalog.v1.CreateProductRequest;
import com.example.superdive.catalog.v1.GetProductRequest;
import com.example.superdive.catalog.v1.ListProductsRequest;
import com.example.superdive.catalog.v1.RequestedLine;
import com.example.superdive.catalog.v1.ResolvePricesRequest;
import com.example.superdive.catalog.v1.ResolvePricesResponse;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

/**
 * Catalog access for the REST layer.
 *
 * Every product read and write now goes over gRPC to superdive-catalog, which
 * owns the `product` table. This class holds no ProductRepository: if it did,
 * the backend would still be a second writer and the service boundary would be
 * decorative.
 */
@Service
public class ProductService {

	private final CatalogServiceGrpc.CatalogServiceBlockingStub catalog;
	private final String currencyCode;

	public ProductService(CatalogServiceGrpc.CatalogServiceBlockingStub catalog,
			@Value("${superdive.catalog.currency:IDR}") String currencyCode) {
		this.catalog = catalog;
		this.currencyCode = currencyCode;
	}

	public ProductDTO createProduct(ProductDTO prodDTO) throws MessageErrorException {
		if (prodDTO == null) {
			throw new MessageErrorException("Product data is required");
		}
		if (prodDTO.getName() == null) {
			throw new MessageErrorException("Product name is required");
		}
		if (prodDTO.getType() == null) {
			throw new MessageErrorException("Product type is required");
		}
		if (prodDTO.getPrice() == null) {
			throw new MessageErrorException("Product price is required");
		}

		CreateProductRequest.Builder request = CreateProductRequest.newBuilder()
				.setName(prodDTO.getName())
				.setType(CatalogMapper.toProtoType(prodDTO.getType()))
				.setPrice(CatalogMapper.toMoney(prodDTO.getPrice(), currencyCode));

		if (prodDTO.getDetails() != null) {
			request.setDetails(prodDTO.getDetails());
		}

		try {
			return CatalogMapper.toDto(catalog.createProduct(request.build()));
		}
		catch (StatusRuntimeException ex) {
			throw asMessageError(ex);
		}
	}

	public List<ProductDTO> getProductsByType(String type) throws MessageErrorException {
		ProductType parsed;
		try {
			parsed = ProductType.valueOf(type);
		}
		catch (IllegalArgumentException ex) {
			throw new MessageErrorException("Unknown product type: " + type);
		}
		return list(ListProductsRequest.newBuilder()
				.setType(CatalogMapper.toProtoType(parsed))
				.build());
	}

	public List<ProductDTO> getAll() throws MessageErrorException {
		// Type left UNSPECIFIED, which catalog reads as "every type".
		return list(ListProductsRequest.getDefaultInstance());
	}

	public ProductDTO getProductById(Long id) throws MessageErrorException {
		try {
			return CatalogMapper.toDto(
					catalog.getProduct(GetProductRequest.newBuilder().setId(id).build()));
		}
		catch (StatusRuntimeException ex) {
			throw asMessageError(ex);
		}
	}

	/**
	 * Prices a basket server-side. Callers send ids and quantities only, so an
	 * order can never be booked against a price the client chose for itself.
	 */
	public ResolvePricesResponse resolvePrices(List<OrdersItemDTO> items) throws MessageErrorException {
		ResolvePricesRequest.Builder request = ResolvePricesRequest.newBuilder();
		for (OrdersItemDTO item : items) {
			request.addLines(RequestedLine.newBuilder()
					.setProductId(item.getProductId() == null ? 0L : item.getProductId())
					.setQty(item.getQty() == null ? 0 : item.getQty())
					.build());
		}

		try {
			return catalog.resolvePrices(request.build());
		}
		catch (StatusRuntimeException ex) {
			throw asMessageError(ex);
		}
	}

	static BigDecimal amountOf(com.example.superdive.catalog.v1.Money money) {
		return CatalogMapper.toBigDecimal(money);
	}

	private List<ProductDTO> list(ListProductsRequest request) throws MessageErrorException {
		List<ProductDTO> products = new ArrayList<>();
		try {
			// Server-streaming call: the blocking stub hands back an iterator that
			// drains as results arrive rather than one buffered response.
			catalog.listProducts(request).forEachRemaining(p -> products.add(CatalogMapper.toDto(p)));
		}
		catch (StatusRuntimeException ex) {
			throw asMessageError(ex);
		}
		return products;
	}

	/**
	 * Collapses a gRPC status into the exception type the REST layer already
	 * handles, so callers are not rewritten to understand gRPC.
	 */
	private MessageErrorException asMessageError(StatusRuntimeException ex) {
		String detail = ex.getStatus().getDescription();

		if (ex.getStatus().getCode() == Status.Code.UNAVAILABLE) {
			return new MessageErrorException("Catalog service is unavailable");
		}
		if (ex.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
			return new MessageErrorException("Catalog service timed out");
		}
		return new MessageErrorException(detail == null ? ex.getStatus().getCode().name() : detail);
	}
}
