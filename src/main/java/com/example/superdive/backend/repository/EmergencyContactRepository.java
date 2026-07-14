package com.example.superdive.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.entity.EmergencyContact;

@Repository
public interface EmergencyContactRepository extends JpaRepository< EmergencyContact,Long> {

}
