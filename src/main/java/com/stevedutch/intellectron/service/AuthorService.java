package com.stevedutch.intellectron.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.repository.AuthorRepository;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepo;

    /**
     * saves the author to the database
     * after checking if firstname is not yet saved.
     * If saved, the author will just be returned
     * @param author
     * @return saved author if firstname wasn't saved,
     * otherwise, the given author will be returned
     */
    public Author saveAuthor(Author author) {
    	
        if (authorRepo.findByAuthorFirstName(author.getAuthorFirstName()) == null) {
                author.setAuthorFirstName(author.getAuthorFirstName());
                return authorRepo.save(author);
            } 
            // else if (authorRepo.findByAuthorFamilyName(author.getAuthorFamilyName()) == null) {
            //     author.setAuthorFamilyName(author.getAuthorFamilyName());
            //     return authorRepo.save(author);
            // } 
        return author;

    }
    // TODO check, ob obige funktion noch genutzt wird ode hierrei sollte oder ...
    public Author saveAuthorWithText(Author author, Tekst tekst) {
    	author.getTexts().add(tekst);
        author = authorRepo.save(author);
        return authorRepo.save(author);
    }

}
