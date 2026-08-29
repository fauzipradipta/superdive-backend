package com.example.superdive.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.superdive.backend.dto.ReferenceDTO;
import com.example.superdive.backend.entity.Reference;
import com.example.superdive.backend.exception.MessageErrorException;
import com.example.superdive.backend.service.ReferenceService;

@RestController
@RequestMapping("/api")
public class ReferenceController {
	
	
	@Autowired
	ReferenceService referenceService; 
	
//	Customer customer;
	public ReferenceController(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}
	
	@PostMapping(value="/reference")
	public ResponseEntity<?> addReference(@RequestBody ReferenceDTO referenceDTO) {
		
		try {
			Reference savedReference = referenceService.addReference(referenceDTO);
			return new ResponseEntity<>(savedReference, HttpStatus.CREATED);
		}catch(MessageErrorException e){
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		}
		
		
	}
	
	
}
