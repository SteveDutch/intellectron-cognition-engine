package com.stevedutch.intellectron.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.Tekst;

@Repository
public interface TextRepository extends JpaRepository<Tekst, Long>{
	
	public Tekst findByText(String text);
	
	@Query("SELECT tekst FROM Tekst tekst WHERE tekst.text LIKE %:searchTerm%")
	public List<Tekst> findTekstByTextFragment(String searchTerm);

}
