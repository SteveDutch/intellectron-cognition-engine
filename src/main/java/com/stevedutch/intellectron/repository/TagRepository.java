package com.stevedutch.intellectron.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	
	public Optional<Tag> findById(Long id);
	
	@Query("SELECT tag FROM Tag tag WHERE tag.tagText LIKE %:searchTerm%")
    public List<Tag> findByTagFragment(@Param("searchTerm") String tagFragment);
	
	// XXX falls ich mal eine genaue Tagsuche brauche
	public Optional<Tag> findByTagText(String name);

}
