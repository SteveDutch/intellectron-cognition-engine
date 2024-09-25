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

	@Query("SELECT tekst FROM Tekst tekst WHERE tekst.id = (SELECT FLOOR(MAX(tekst.id) * RAND()) FROM Tekst tekst) ORDER BY tekst.id LIMIT 1")
//	@Query(value = "SELECT tekst FROM texts tekst WHERE text_id = (SELECT FLOOR(MAX(text_id) * RAND()) FROM texts tekst) ORDER BY text_id LIMIT 1;", 
//			nativeQuery = true)
	public Tekst findOneRandomTekst();

}
