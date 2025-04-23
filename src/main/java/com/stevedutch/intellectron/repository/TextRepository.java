package com.stevedutch.intellectron.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.Tekst;

@Repository
public interface TextRepository extends JpaRepository<Tekst, Long>{
	
	public Tekst findByText(String text);
	
	// Find by hash (should be fast due to index)
	Optional<Tekst> findByHash(String hash);
	
	/**
	 * Finds a page of Tekst entities, ordered by textId in descending order.
	 * Spring Data JPA automatically generates the query based on the method name.
	 * @param pageable Contains pagination information (page number, size).
	 * @return A list of Tekst entities for the requested page.
	 */
	List<Tekst> findByOrderByTextIdDesc(Pageable pageable);

	// Native query used for performance and simplicity during refactoring
	// XXX: Consider introducing DTO in future iterations if needed
	// Note: This native query might not map correctly to the Tekst entity if fields are missing (like 'hash')
	// Consider using JPQL or mapping the result to a DTO/Projection if issues arise.
	@Query(value = "SELECT text_id, SUBSTRING(tekst, 1, :maxLength) as tekst, title, tekstdato, source " +
            "FROM texts " +
            "WHERE tekst LIKE CONCAT('%', :searchTerm, '%')", 
    nativeQuery = true)
	public List<Tekst> findTruncatedTekstByTextFragment(String searchTerm, @Param("maxLength") int maxLength);

	
	
	@Query(value = "SELECT text_id, SUBSTRING(tekst, 1, :maxLength) as tekst, title, tekstdato, source " + 
                   "FROM texts " +
                   "WHERE texts.text_id = :textId",
           nativeQuery = true)
	Optional<Tekst> findTruncatedTextbyId(@Param("textId") Long textId, @Param("maxLength") int maxLength);

	
//	@Query(value = "SELECT SUBSTRING(text, 1, :maxLength) FROM tekst WHERE text LIKE %:searchTerm%", nativeQuery = true)
//	List<String> findTruncatedTekstByTextFragment(@Param("searchTerm") String searchTerm, @Param("maxLength") int maxLength);
//	
	
	
@Query("SELECT tekst FROM Tekst tekst WHERE tekst.id = (SELECT FLOOR(MAX(tekst.id) * RAND()) FROM Tekst tekst) ORDER BY tekst.id LIMIT 1")
public Tekst findOneRandomTekst();
	//@Query("SELECT t FROM Tekst t WHERE t.textId = (SELECT FLOOR(MAX(t2.textId) * RAND()) FROM Tekst t2) ORDER BY t.textId LIMIT 1")
	//Optional<Tekst> findOneRandomTekst();


}
