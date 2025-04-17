package com.example.superdive.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.superdive.backend.Entity.Reference;
import com.example.superdive.backend.Repository.ReferenceRepository;
import com.example.superdive.backend.dto.ReferenceDTO;
import com.example.superdive.backend.exception.MessageErrorException;

@Service
public class ReferenceService {
	
	@Autowired
	private ReferenceRepository referenceRepository;
	
	public Reference addReference(ReferenceDTO referenceDTO)throws MessageErrorException {
		Reference reference = new Reference(); 
		reference.setReferenceName(referenceDTO.getReferenceName());
		reference.setReferencePhoneNum(referenceDTO.getPhoneNum());
				
	
		if (referenceDTO.getReferenceName() == null || referenceDTO.getReferenceName().isEmpty()) {
            throw new MessageErrorException("Reference name cannot be empty.");
        }
		return referenceRepository.save(reference);
	}
}
