package com.example.superdive.backend.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.superdive.backend.dto.ProductDTO;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.enums.ProductType;
import com.example.superdive.backend.service.ProductService;

@RestController
@RequestMapping("/api")


public class ProductController {
	@Autowired
	private ProductService productService;
	
	public ProductController(ProductService productService) {
		this.productService = productService;
	}
	
	@PostMapping(value="/products")
	public List<ProductDTO>getProductByType(@RequestParam String type){
		
		return productService.getProductsByType(type).stream().map(p ->{
			ProductDTO dto = new ProductDTO();
			dto.setId(p.getId());
			dto.setType(p.getType());
			dto.setDetails(p.getDetails()); 
			dto.setPrice(p.getPrice());
			return dto;
		}).collect(Collectors.toList());
	}
	
	@GetMapping("/product-types")
    public List<ProductType> getAllProductTypes() {
        return Arrays.asList(ProductType.values());
    }

	@GetMapping("/all-products")
	public List <Product> getAll(){
		return productService.getAll(); 
	}
}
