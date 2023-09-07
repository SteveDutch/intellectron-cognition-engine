package com.stevedutch.intellectron.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stevedutch.intellectron.domain.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
	
	public Author findByAuthorFirstName(String name);

}
