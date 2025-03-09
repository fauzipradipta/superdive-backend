package com.example.superdive.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.Entity.EmergencyContact;

@Repository
public interface EmergencyContactRepository extends JpaRepository< EmergencyContact,Long> {

}
