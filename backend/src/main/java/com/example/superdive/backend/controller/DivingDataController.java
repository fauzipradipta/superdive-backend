package com.example.superdive.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.superdive.backend.Entity.DivingData;
import com.example.superdive.backend.Service.DivingDataService;

@RestController
@RequestMapping("/api")
public class DivingDataController {
	@Autowired
	DivingDataService divingDataService;
	
	public DivingData saveDivingData(@RequestBody DivingData divingData ) {
		
		return divingDataService.saveDivingData(divingData);
	}
	
}
