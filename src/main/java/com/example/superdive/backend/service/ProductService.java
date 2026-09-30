package com.example.superdive.backend.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.superdive.backend.dto.OrdersItemDTO;
import com.example.superdive.backend.dto.PricedLineDTO;
import com.example.superdive.backend.dto.ProductDTO;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.enums.ProductType;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.repository.ProductRepository;

/**
 * Product access for the REST layer.
 *
 * Reads and writes the `product` table through ProductRepository. Callers get a
 * ProductDTO rather than the entity, so a JPA association can never be dragged
 * into a response body by the serialiser.
 */
@Service
public class ProductService {

	private final ProductRepository productRepo;

	public ProductService(ProductRepository productRepo) {
		this.productRepo = productRepo;
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

		Product product = new Product();
		product.setName(prodDTO.getName());
		product.setType(prodDTO.getType());
		product.setDetails(prodDTO.getDetails());
		product.setPrice(prodDTO.getPrice());

		return toDto(productRepo.save(product));
	}

	public List<ProductDTO> getProductsByType(String type) throws MessageErrorException {
		ProductType parsed;
		try {
			parsed = ProductType.valueOf(type);
		}
		catch (IllegalArgumentException ex) {
			throw new MessageErrorException("Unknown product type: " + type);
		}
		return toDtos(productRepo.findByType(parsed));
	}

	public List<ProductDTO> getAll() {
		return toDtos(productRepo.findAll());
	}

	public ProductDTO getProductById(Long id) throws MessageErrorException {
		return toDto(productRepo.findById(id)
				.orElseThrow(() -> new MessageErrorException("Product not found with id: " + id)));
	}

	/**
	 * Prices a basket server-side. Callers send ids and quantities only, so an
	 * order can never be booked against a price the client chose for itself.
	 *
	 * Every unknown id is collected before throwing, so a caller sending five
	 * bad ids is told about all five instead of discovering them one request at
	 * a time.
	 */
	public List<PricedLineDTO> resolvePrices(List<OrdersItemDTO> items) throws MessageErrorException {
		List<Long> requestedIds = new ArrayList<>();
		for (OrdersItemDTO item : items) {
			// Checked here rather than trusted from the caller: a null in the
			// list would otherwise reach findAllById, which rejects it with a
			// data-access exception instead of a message a client can read.
			if (item.getProductId() == null) {
				throw new MessageErrorException("Product id is required for every order line");
			}
			requestedIds.add(item.getProductId());
		}

		// One findAllById rather than a findById per line: a twenty-line order
		// would otherwise issue twenty queries to price itself.
		Map<Long, Product> found = new LinkedHashMap<>();
		for (Product product : productRepo.findAllById(requestedIds)) {
			found.put(product.getId(), product);
		}

		List<Long> missingIds = new ArrayList<>();
		List<PricedLineDTO> lines = new ArrayList<>();

		for (OrdersItemDTO item : items) {
			Product product = found.get(item.getProductId());
			if (product == null) {
				missingIds.add(item.getProductId());
				continue;
			}
			lines.add(new PricedLineDTO(product.getId(), item.getQty(), product.getPrice()));
		}

		if (!missingIds.isEmpty()) {
			throw new MessageErrorException("Unknown product ids: " + missingIds);
		}

		return lines;
	}

	private static List<ProductDTO> toDtos(List<Product> products) {
		List<ProductDTO> dtos = new ArrayList<>(products.size());
		for (Product product : products) {
			dtos.add(toDto(product));
		}
		return dtos;
	}

	private static ProductDTO toDto(Product product) {
		return new ProductDTO(
				product.getId(),
				product.getName(),
				product.getType(),
				product.getDetails(),
				product.getPrice());
	}
}
