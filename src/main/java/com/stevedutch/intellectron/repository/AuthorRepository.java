package com.stevedutch.intellectron.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
	
	public Author findByAuthorFirstName(String name);

	public Author findByAuthorFamilyName(String name);
	
	public List<Author> findByAuthorFamilyNameLike(String name);
	
	public Author findByAuthorFirstNameAndAuthorFamilyName(String name1, String name2);
	
	
	

}
