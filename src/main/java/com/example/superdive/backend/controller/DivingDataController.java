package com.example.superdive.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.superdive.backend.entity.DivingData;
import com.example.superdive.backend.service.DivingDataService;

@RestController
@RequestMapping("/api")
public class DivingDataController {
	@Autowired
	DivingDataService divingDataService;
	
	@PostMapping("/diving-data")
	public DivingData saveDivingData(@RequestBody DivingData divingData ) {
		
		return divingDataService.saveDivingData(divingData);
	}
	
}
