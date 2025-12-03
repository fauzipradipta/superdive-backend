package com.example.superdive.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.dto.ProductDTO;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.enums.ProductType;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.repository.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	public ProductService(ProductRepository productRepository) {
		// TODO Auto-generated constructor stub
		this.productRepository = productRepository;
	}
	
	public Product createProduct(ProductDTO productDTO) throws MessageErrorException {
		
	if (productDTO.getName() == null || productDTO.getName().isEmpty()) {
		throw new MessageErrorException("Product name cannot be null or empty");
	}
	if (productDTO.getType() == null) {
	throw new MessageErrorException("Product type cannot be null");
    }
    if (productDTO.getDetails() == null || productDTO.getDetails().isEmpty()) {
        throw new MessageErrorException("Product details cannot be null or empty");
    }
    if (productDTO.getPrice() == null) {
        throw new MessageErrorException("Product price cannot be null");
    }
		Product product = new Product(); 
		product.setType(productDTO.getType());
		product.setDetails(productDTO.getDetails());
		product.setPrice(productDTO.getPrice());
		
		return productRepository.save(product);
	}
	
	public List<Product> getProductsByType(String type) {
	        return productRepository.findByType(ProductType.valueOf(type));
	    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    public List <Product>getAll(){
    	return productRepository.findAll(); 
    }
}
