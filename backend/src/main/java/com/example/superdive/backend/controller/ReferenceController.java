package com.example.superdive.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.superdive.backend.Entity.Reference;
import com.example.superdive.backend.Service.ReferenceService;

@Controller
@RequestMapping("/api")
@CrossOrigin(origins ="http://localhost:3000")
public class ReferenceController {
	
	
	@Autowired
	ReferenceService referenceService; 
	
	@PostMapping("/reference")
	public Reference addReference(@RequestBody Reference reference) {
		return referenceService.addReference(reference);
	}
	
	
}
