package com.example.superdive.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.entity.DivingData;
import com.example.superdive.backend.repository.DivingDataRepository;

@Service
public class DivingDataService {

	@Autowired
	DivingDataRepository divingDataRepo;
	
	public DivingData saveDivingData(DivingData divingData) {
		return divingDataRepo.save(divingData);
	}
}
