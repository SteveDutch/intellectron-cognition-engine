package com.stevedutch.intellectron.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.ZettelTag;

@Repository
public interface TagRepository extends JpaRepository<ZettelTag, Long> {
	
	public Optional<ZettelTag> findById(Long id);

}
