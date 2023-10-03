package com.stevedutch.intellectron.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.repository.AuthorRepository;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepo;

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

}
