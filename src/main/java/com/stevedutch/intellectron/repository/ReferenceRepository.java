package com.stevedutch.intellectron.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.stevedutch.intellectron.domain.Reference;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference, Long> {
	
	public List<Reference> findAll();
	
	// Fix: Use the correct field names that actually exist in the Reference entity
	public Reference findBySourceZettelIdAndTargetZettelId(Long sourceZettelId, Long targetZettelId);
	
    List<Reference> findAllBySourceZettelId(Long sourceZettelId);

    // Orphan list: pointers without an owning zettel_references row (ignores @Where)
    @Query(value = """
      SELECT p.* 
      FROM pointer p 
      LEFT JOIN zettel_references zr ON zr.reference_id = p.reference_id
      WHERE zr.zettel_id IS NULL
    """, nativeQuery = true)
    List<Reference> findOrphansIncludingSoftDeleted();

    // Purge: permanently delete soft-deleted references older than N days
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM pointer WHERE deleted = true AND deleted_at < DATE_SUB(NOW(), INTERVAL ?1 DAY)", nativeQuery = true)
    int purgeSoftDeletedOlderThanDays(int days);
}
