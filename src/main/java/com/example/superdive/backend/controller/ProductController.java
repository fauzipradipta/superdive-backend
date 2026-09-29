package com.example.superdive.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.superdive.backend.dto.ProductDTO;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

	@Autowired
	private ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping(value = "/products")
	public List<ProductDTO> postProduct(@RequestParam String type) throws MessageErrorException {
		// Catalog already returns exactly these fields, so the hand-rolled
		// entity-to-DTO copy that used to live here is gone.
		return productService.getProductsByType(type);
	}

	/*
	 * Returns ProductDTO rather than the Product entity it used to serialise.
	 * The backend no longer has a product entity to hand out, and the entity
	 * form was leaking a `customer` association that was NULL on every row.
	 */
	@GetMapping("/all-products")
	public List<ProductDTO> getAll() throws MessageErrorException {
		return productService.getAll();
	}
}
