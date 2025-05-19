package com.stevedutch.intellectron.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.Tekst;

@Repository
public interface TextRepository extends JpaRepository<Tekst, Long>{
	
	public Tekst findByText(String text);
	
	/**
	 * Finds a Tekst entity by its exact title and text content.
	 *
	 * @param title The title of the Tekst.
	 * @param text The text content of the Tekst.
	 * @return The Tekst entity if found, otherwise null.
	 */
	public Tekst findByTitleAndText(String title, String text);
	
	// Native query used for performance and simplicity during refactoring
	// XXX: Consider introducing DTO in future iterations if needed
	@Query(value = "SELECT text_id, SUBSTRING(tekst, 1, :maxLength) as tekst, title, tekstdato, source " +
            "FROM texts " +
            "WHERE tekst LIKE CONCAT('%', :searchTerm, '%')", 
    nativeQuery = true)
	public List<Tekst> findTruncatedTekstByTextFragment(String searchTerm, @Param("maxLength") int maxLength);

	
	
	@Query(value = "SELECT text_id, SUBSTRING(tekst, 1, :maxLength) as tekst, title, tekstdato, source " + "FROM texts "
			+ "WHERE texts.text_id LIKE :textId", nativeQuery = true)
	public Tekst findTruncatedTextbyId(Long textId, int maxLength);

	
//	@Query(value = "SELECT SUBSTRING(text, 1, :maxLength) FROM tekst WHERE text LIKE %:searchTerm%", nativeQuery = true)
//	List<String> findTruncatedTekstByTextFragment(@Param("searchTerm") String searchTerm, @Param("maxLength") int maxLength);
//	
	
	
	
	@Query("SELECT tekst FROM Tekst tekst WHERE tekst.id = (SELECT FLOOR(MAX(tekst.id) * RAND()) FROM Tekst tekst) ORDER BY tekst.id LIMIT 1")
	public Tekst findOneRandomTekst();


}
