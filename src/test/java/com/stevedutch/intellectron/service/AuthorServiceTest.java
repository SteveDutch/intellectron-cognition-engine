package com.stevedutch.intellectron.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.repository.AuthorRepository;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepo;

    @Mock
    private TextService textService;

    @InjectMocks
    private AuthorService authorService;
  
    @Mock
    private SearchService searchService;

    @BeforeEach
    public void setUp() {
        // Mocks are initialized by @ExtendWith(MockitoExtension.class)
//    	MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveAuthorIfUnknown() {
        // Arrange
        Author author = new Author("John", "Doe");
        when(authorRepo.findByAuthorFirstNameAndAuthorFamilyName("John", "Doe")).thenReturn(null);
        when(authorRepo.save(author)).thenReturn(author);

        // Act
        Author savedAuthor = authorService.saveAuthor(author);

        // Assert
        verify(authorRepo, times(1)).findByAuthorFirstNameAndAuthorFamilyName("John", "Doe");
        verify(authorRepo, times(1)).save(author);
        assertEquals(author, savedAuthor);
    }

    @Test
    public void testSaveAuthorIfUnknownExistingAuthor() {
        // Arrange
        Author author = new Author("John", "Doe");
        when(authorRepo.findByAuthorFirstNameAndAuthorFamilyName("John", "Doe")).thenReturn(author);

        // Act
        Author savedAuthor = authorService.saveAuthor(author);

        // Assert
        verify(authorRepo, times(1)).findByAuthorFirstNameAndAuthorFamilyName("John", "Doe");
        verify(authorRepo, never()).save(author);
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

        when(searchService.findByText("Sample text")).thenReturn(tekst);
        when(authorRepo.findByAuthorFirstNameAndAuthorFamilyName("John", "Doe")).thenReturn(null);
        when(authorRepo.save(author)).thenReturn(author);

        // Act
        Author savedAuthor = authorService.saveAuthorWithText(author, tekst);

        // Assert
        verify(searchService, times(1)).findByText("Sample text");
        verify(authorRepo, times(3)).findByAuthorFirstNameAndAuthorFamilyName("John", "Doe");
        verify(authorRepo, times(2)).save(author);
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

        when(searchService.findByText("Sample text")).thenReturn(tekst);
        when(authorRepo.findByAuthorFirstNameAndAuthorFamilyName("John", "Doe")).thenReturn(author);

        // Act
        Author savedAuthor = authorService.saveAuthorWithText(author, tekst);

        // Assert
        verify(searchService, times(1)).findByText("Sample text");
        verify(authorRepo, times(2)).findByAuthorFirstNameAndAuthorFamilyName("John", "Doe");
        verify(textService, times(1)).saveTextWithAuthor(tekst, author);
        assertEquals(author, savedAuthor);
    }
    
    @Test
    void testConnectAuthorWithText() {
        // Arrange
        Author author = new Author("Paul", "Breitner");
        Tekst tekst = new Tekst("super text");
        
        when(authorRepo.findByAuthorFirstNameAndAuthorFamilyName("Paul", "Breitner"))
            .thenReturn(null);

        // Act
        Author result = authorService.connectAuthorWithText(author, tekst);

        // Assert
        assertNotNull(result);
        assertEquals("Paul", result.getAuthorFirstName());
        assertEquals("Breitner", result.getAuthorFamilyName());
        assertTrue(result.getTexts().contains(tekst));
        assertEquals(result, tekst.getAssociatedAuthors().get(0));
    }

    @Test
    void testConnectAuthorWithTextExistingAuthor() {
        // Arrange
        Author existingAuthor = new Author("Fritz", "Schuhmacher");
        Tekst tekst = new Tekst("test text");
        
        when(authorRepo.findByAuthorFirstNameAndAuthorFamilyName("Fritz", "Schuhmacher"))
            .thenReturn(existingAuthor);

        // Act
        Author result = authorService.connectAuthorWithText(existingAuthor, tekst);

        // Assert
        assertNotNull(result);
        assertEquals("Fritz", result.getAuthorFirstName());
        assertEquals("Schuhmacher", result.getAuthorFamilyName());
        assertTrue(result.getTexts().contains(tekst));
        assertEquals(result, tekst.getAssociatedAuthors().get(0));
    }


    @Test
    public void testFindAuthorByLastNameLike() {
        // Arrange
        List<Author> authors = new ArrayList<>();
        authors.add(new Author("John", "Doe"));
        authors.add(new Author("Jane", "Doe"));

        when(searchService.findAuthorByLastNameLike("Doe")).thenReturn(authors);

        // Act
        List<Author> result = searchService.findAuthorByLastNameLike("Doe");

        // Assert
        verify(searchService, times(1)).findAuthorByLastNameLike("Doe");
        assertEquals(authors, result);
    }
}