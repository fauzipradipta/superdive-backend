package com.example.superdive.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.superdive.backend.Entity.Reference;
import com.example.superdive.backend.Service.ReferenceService;
import com.example.superdive.backend.dto.ReferenceDTO;
import com.example.superdive.backend.exception.MessageErrorException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins ="http://localhost:3000")
public class ReferenceController {
	
	
	@Autowired
	ReferenceService referenceService; 
	
//	Customer customer;
	@Autowired
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
