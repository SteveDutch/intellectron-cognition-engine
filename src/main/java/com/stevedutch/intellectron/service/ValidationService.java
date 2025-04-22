package com.stevedutch.intellectron.service;

import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Author;

@Service
public class ValidationService {
    
    private static final String DEFAULT_AUTHOR_FAMILY_NAME = "Unbekannt";
    private static final String DEFAULT_AUTHOR_FIRST_NAME = "Ignotus";
    
    /**
     * Ensures author names are present, applying default values if not. If author is null a new author with the default names is returned
     *  
     * @param author -  author to check
     * @return author   
     */
    public Author ensureAuthorNames(Author author) {
        if (author == null) {
            author = new Author();
            author.setAuthorFirstName(DEFAULT_AUTHOR_FIRST_NAME);
            author.setAuthorFamilyName(DEFAULT_AUTHOR_FAMILY_NAME);
            return author;
        }
        
        if (author.getAuthorFamilyName() == null || author.getAuthorFamilyName().isEmpty()
                || author.getAuthorFamilyName().trim().isBlank()) {
            author.setAuthorFamilyName(DEFAULT_AUTHOR_FAMILY_NAME);
        }
        
        if (author.getAuthorFirstName() == null || author.getAuthorFirstName().isEmpty()
                || author.getAuthorFirstName().trim().isBlank()) {
            author.setAuthorFirstName(DEFAULT_AUTHOR_FIRST_NAME);
        }
        return author;
    }
} 