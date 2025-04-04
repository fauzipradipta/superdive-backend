package com.example.superdive.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.Entity.Reference;
import com.example.superdive.backend.Repository.ReferenceRepository;

@Service
public class ReferenceService {
	
	@Autowired
	private ReferenceRepository referenceRepository;
	
	public Reference addReference(Reference reference) {
		
		return referenceRepository.save(reference);
	}
}
