package com.stevedutch.intellectron.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.Reference;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference, Long> {
	
	public List<Reference> findAll();
	
}
