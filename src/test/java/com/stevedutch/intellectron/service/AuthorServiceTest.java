package com.stevedutch.intellectron.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.repository.AuthorRepository;


public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private TextService textService;

    @InjectMocks
    private AuthorService authorService;

    @Before
    public void setUp() {
    	MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveAuthorIfUnknown() {
        // Arrange
        Author author = new Author("John", "Doe");
        when(authorRepository.findByAuthorFirstNameAndAuthorFamilyName("John", "Doe")).thenReturn(null);
        when(authorRepository.save(author)).thenReturn(author);

        // Act
        Author savedAuthor = authorService.saveAuthorIfUnknown(author);

        // Assert
        verify(authorRepository, times(1)).findByAuthorFirstNameAndAuthorFamilyName("John", "Doe");
        verify(authorRepository, times(1)).save(author);
        assertEquals(author, savedAuthor);
    }

    @Test
    public void testSaveAuthorIfUnknownExistingAuthor() {
        // Arrange
        Author author = new Author("John", "Doe");
        when(authorRepository.findByAuthorFirstNameAndAuthorFamilyName("John", "Doe")).thenReturn(author);

        // Act
        Author savedAuthor = authorService.saveAuthorIfUnknown(author);

        // Assert
        verify(authorRepository, times(1)).findByAuthorFirstNameAndAuthorFamilyName("John", "Doe");
        verify(authorRepository, never()).save(author);
        assertEquals(author, savedAuthor);
    }

    @Test
    public void testSaveAuthorWithText() {
        // Arrange
        Author author = new Author("John", "Doe");
        Tekst tekst = new Tekst("Sample text");
        List<Tekst> texts = new ArrayList<>();
        texts.add(tekst);
        author.setTexts(texts);

        when(textService.findByText("Sample text")).thenReturn(tekst);
        when(authorRepository.findByAuthorFirstNameAndAuthorFamilyName("John", "Doe")).thenReturn(null);
        when(authorRepository.save(author)).thenReturn(author);

        // Act
        Author savedAuthor = authorService.saveAuthorWithText(author, tekst);

        // Assert
        verify(textService, times(1)).findByText("Sample text");
        verify(authorRepository, times(2)).findByAuthorFirstNameAndAuthorFamilyName("John", "Doe");
        verify(authorRepository, times(2)).save(author);
        verify(textService, times(1)).saveTextWithAuthor(tekst, author);
        assertEquals(author, savedAuthor);
    }

    @Test
    public void testSaveAuthorWithTextExistingAuthor() {
        // Arrange
        Author author = new Author("John", "Doe");
        Tekst tekst = new Tekst("Sample text");
        List<Tekst> texts = new ArrayList<>();
        texts.add(tekst);
        author.setTexts(texts);

        when(textService.findByText("Sample text")).thenReturn(tekst);
        when(authorRepository.findByAuthorFirstNameAndAuthorFamilyName("John", "Doe")).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);

        // Act
        Author savedAuthor = authorService.saveAuthorWithText(author, tekst);

        // Assert
        verify(textService, times(1)).findByText("Sample text");
        verify(authorRepository, times(1)).findByAuthorFirstNameAndAuthorFamilyName("John", "Doe");
        verify(textService, times(1)).saveTextWithAuthor(tekst, author);
        assertEquals(author, savedAuthor);
    }

    @Test
    public void testFindAuthorByLastNameLike() {
        // Arrange
        List<Author> authors = new ArrayList<>();
        authors.add(new Author("John", "Doe"));
        authors.add(new Author("Jane", "Doe"));

        when(authorRepository.findByAuthorFamilyNameLike("Doe")).thenReturn(authors);

        // Act
        List<Author> result = authorService.findAuthorByLastNameLike("Doe");

        // Assert
        verify(authorRepository, times(1)).findByAuthorFamilyNameLike("Doe");
        assertEquals(authors, result);
    }
}
