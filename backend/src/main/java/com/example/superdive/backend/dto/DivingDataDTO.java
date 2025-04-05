package com.example.superdive.backend.dto;

import java.time.LocalDate;
import java.util.List;

public class DivingDataDTO {
	 private String name;
	    private String email;
	    private String phoneNum;
	    private LocalDate dob;
	    private boolean isDiver;
	    private List<DivingDataDTO> divingData;
	    private List<ReferenceDTO> reference;
}
