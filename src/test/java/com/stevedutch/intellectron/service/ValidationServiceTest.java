package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stevedutch.intellectron.domain.Author;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @InjectMocks
    private ValidationService validationService;
    
    @Test
    @DisplayName("Should return a new author with default names when null author is provided")
    void ensureAuthorNames_NullAuthor_ReturnsNewAuthorWithDefaultNames() {
        // Arrange
        Author author = null;
        
        // Act
        Author result = validationService.ensureAuthorNames(author);
        
        // Assert
        assertNotNull(result);
        assertEquals("Ignotus", result.getAuthorFirstName());
        assertEquals("Unbekannt", result.getAuthorFamilyName());
    }
    
    @Test
    @DisplayName("Should set default family name when family name is null")
    void ensureAuthorNames_NullFamilyName_SetsDefaultFamilyName() {
        // Arrange
        Author author = new Author();
        author.setAuthorFirstName("John");
        author.setAuthorFamilyName(null);
        
        // Act
        Author result = validationService.ensureAuthorNames(author);
        
        // Assert
        assertEquals("John", result.getAuthorFirstName());
        assertEquals("Unbekannt", result.getAuthorFamilyName());
    }
    
    @Test
    @DisplayName("Should set default family name when family name is empty")
    void ensureAuthorNames_EmptyFamilyName_SetsDefaultFamilyName() {
        // Arrange
        Author author = new Author();
        author.setAuthorFirstName("John");
        author.setAuthorFamilyName("");
        
        // Act
        Author result = validationService.ensureAuthorNames(author);
        
        // Assert
        assertEquals("John", result.getAuthorFirstName());
        assertEquals("Unbekannt", result.getAuthorFamilyName());
    }
    
    @Test
    @DisplayName("Should set default family name when family name is blank")
    void ensureAuthorNames_BlankFamilyName_SetsDefaultFamilyName() {
        // Arrange
        Author author = new Author();
        author.setAuthorFirstName("John");
        author.setAuthorFamilyName("   ");
        
        // Act
        Author result = validationService.ensureAuthorNames(author);
        
        // Assert
        assertEquals("John", result.getAuthorFirstName());
        assertEquals("Unbekannt", result.getAuthorFamilyName());
    }
    
    @Test
    @DisplayName("Should set default first name when first name is null")
    void ensureAuthorNames_NullFirstName_SetsDefaultFirstName() {
        // Arrange
        Author author = new Author();
        author.setAuthorFirstName(null);
        author.setAuthorFamilyName("Doe");
        
        // Act
        Author result = validationService.ensureAuthorNames(author);
        
        // Assert
        assertEquals("Ignotus", result.getAuthorFirstName());
        assertEquals("Doe", result.getAuthorFamilyName());
    }
    
    @Test
    @DisplayName("Should set default first name when first name is empty")
    void ensureAuthorNames_EmptyFirstName_SetsDefaultFirstName() {
        // Arrange
        Author author = new Author();
        author.setAuthorFirstName("");
        author.setAuthorFamilyName("Doe");
        
        // Act
        Author result = validationService.ensureAuthorNames(author);
        
        // Assert
        assertEquals("Ignotus", result.getAuthorFirstName());
        assertEquals("Doe", result.getAuthorFamilyName());
    }
    
    @Test
    @DisplayName("Should set default first name when first name is blank")
    void ensureAuthorNames_BlankFirstName_SetsDefaultFirstName() {
        // Arrange
        Author author = new Author();
        author.setAuthorFirstName("  ");
        author.setAuthorFamilyName("Doe");
        
        // Act
        Author result = validationService.ensureAuthorNames(author);
        
        // Assert
        assertEquals("Ignotus", result.getAuthorFirstName());
        assertEquals("Doe", result.getAuthorFamilyName());
    }
    
    @Test
    @DisplayName("Should not modify valid author names")
    void ensureAuthorNames_ValidNames_DoesNotModifyNames() {
        // Arrange
        Author author = new Author();
        author.setAuthorFirstName("John");
        author.setAuthorFamilyName("Doe");
        
        // Act
        Author result = validationService.ensureAuthorNames(author);
        
        // Assert
        assertEquals("John", result.getAuthorFirstName());
        assertEquals("Doe", result.getAuthorFamilyName());
        assertEquals(author, result);
    }
} 