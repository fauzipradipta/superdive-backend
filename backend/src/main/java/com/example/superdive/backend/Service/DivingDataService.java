package com.example.superdive.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.Entity.DivingData;
import com.example.superdive.backend.Repository.DivingDataRepository;

@Service
public class DivingDataService {

	@Autowired
	DivingDataRepository divingDataRepo;
	
	public DivingData saveDivingData(DivingData divingData) {
		return divingDataRepo.save(divingData);
	}
}
