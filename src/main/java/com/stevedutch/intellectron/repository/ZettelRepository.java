package com.stevedutch.intellectron.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.Zettel;

@Repository
public interface ZettelRepository extends JpaRepository<Zettel, Long>{
	
	public Optional<Zettel> findById(Long Id);
	
}
