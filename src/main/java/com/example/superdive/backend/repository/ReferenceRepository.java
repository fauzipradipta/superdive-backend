package com.example.superdive.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.entity.Reference;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference, Long>{

}
