package com.example.superdive.backend.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.Entity.Product;
import com.example.superdive.backend.Repository.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepo;
	
	
	 public List<Product> getProductsByType(String type) {
	        return productRepo.findByType(type);
	    }

	    public Product getProductById(Long id) {
	        return productRepo.findById(id)
	                .orElseThrow(() -> new RuntimeException("Product not found"));
	    }
}
