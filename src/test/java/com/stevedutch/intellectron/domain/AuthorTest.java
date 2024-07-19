package com.stevedutch.intellectron.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthorTest {

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author("John", "Doe");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("John", author.getAuthorFirstName());
        assertEquals("Doe", author.getAuthorFamilyName());
        assertNull(author.getAuthorId());
        assertTrue(author.getTexts().isEmpty());
    }

    @Test
    void testSetters() {
        author.setAuthorId(1L);
        author.setAuthorFirstName("Jane");
        author.setAuthorFamilyName("Smith");
        List<Tekst> texts = new ArrayList<>();
        texts.add(new Tekst());
        author.setTexts(texts);

        assertEquals(1L, author.getAuthorId());
        assertEquals("Jane", author.getAuthorFirstName());
        assertEquals("Smith", author.getAuthorFamilyName());
        assertEquals(1, author.getTexts().size());
    }

    @Test
    void testToString() {
        author.setAuthorId(1L);
        String expected = "Author \n [authorId=1, \n authorFirstName=John, authorFamilyName=Doe, \n Anzahl der Texte =Optional[0]]";
        assertEquals(expected, author.toString());
    }

    @Test
    void testHashCode() {
        Author sameAuthor = new Author("John", "Doe");
        assertEquals(author.hashCode(), sameAuthor.hashCode());

        Author differentAuthor = new Author("Jane", "Smith");
        assertNotEquals(author.hashCode(), differentAuthor.hashCode());
    }

    @Test
    void testEquals() {
        Author sameAuthor = new Author("John", "Doe");
        Author differentAuthor = new Author("Jane", "Smith");

        assertTrue(author.equals(author));
        assertTrue(author.equals(sameAuthor));
        assertFalse(author.equals(differentAuthor));
        assertFalse(author.equals(null));
        assertFalse(author.equals(new Object()));
    }

    @Test
    void testEqualsWithDifferentFields() {
        Author authorWithId = new Author("John", "Doe");
        authorWithId.setAuthorId(1L);

        assertFalse(author.equals(authorWithId));

        Author authorWithTexts = new Author("John", "Doe");
        authorWithTexts.getTexts().add(new Tekst());

        assertFalse(author.equals(authorWithTexts));
    }
}
