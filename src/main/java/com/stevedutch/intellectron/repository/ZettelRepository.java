package com.stevedutch.intellectron.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stevedutch.intellectron.domain.Zettel;

public interface ZettelRepository extends JpaRepository<Zettel, Long>{
	
	public Zettel findByZettelId(Long Id);
	

}
