package com.example.superdive.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.dto.ProductDTO;
import com.example.superdive.backend.entity.Product;
import com.example.superdive.backend.enums.ProductType;
import com.example.superdive.backend.repository.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	public ProductService(ProductRepository productRepository) {
		// TODO Auto-generated constructor stub
		this.productRepository = productRepository;
	}
	
	public Product createProduct(ProductDTO prodDTO) {
		Product prod = new Product(); 
		prod.setType(prodDTO.getType());
		prod.setDetails(prodDTO.getDetails());
		prod.setPrice(prodDTO.getPrice());
		
		return productRepository.save(prod);
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
