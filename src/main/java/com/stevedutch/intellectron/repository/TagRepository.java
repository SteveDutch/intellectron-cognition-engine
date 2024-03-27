package com.stevedutch.intellectron.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	
	public Optional<Tag> findById(Long id);
	
	public Optional<Tag> findByTagText(String name);

}
