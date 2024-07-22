package com.stevedutch.intellectron.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class TekstTest {

    private Tekst tekst;

    @BeforeEach
    void setUp() {
        tekst = new Tekst("Sample text");
    }

    @Test
    void testConstructor() {
        assertNotNull(tekst);
        assertEquals("Sample text", tekst.getText());
        assertEquals(LocalDate.EPOCH, tekst.getTextDate());
    }

    @Test
    void testSetAndGetTextId() {
        tekst.setTextId(1L);
        assertEquals(1L, tekst.getTextId());
    }

    @Test
    void testSetAndGetText() {
        tekst.setText("New text");
        assertEquals("New text", tekst.getText());
    }

    @Test
    void testSetAndGetTitle() {
        tekst.setTitle("Sample Title");
        assertEquals("Sample Title", tekst.getTitle());
    }

    @Test
    void testSetAndGetTextDate() {
        LocalDate date = LocalDate.now();
        tekst.setTextDate(date);
        assertEquals(date, tekst.getTextDate());
    }

    @Test
    void testSetAndGetSource() {
        tekst.setSource("Sample Source");
        assertEquals("Sample Source", tekst.getSource());
    }

    @Test
    void testSetAndGetZettels() {
        List<Zettel> zettels = new ArrayList<>();
        zettels.add(new Zettel());
        tekst.setZettels(zettels);
        assertEquals(zettels, tekst.getZettels());
    }

    @Test
    void testGetZettelsInitialization() {
        assertNotNull(tekst.getZettels());
        assertTrue(tekst.getZettels().isEmpty());
    }

    @Test
    void testSetAndGetAssociatedAuthors() {
        List<Author> authors = new ArrayList<>();
        authors.add(new Author());
        tekst.setAssociatedAuthors(authors);
        assertEquals(authors, tekst.getAssociatedAuthors());
    }

    @Test
    void testAddAssociatedAuthor() {
        Author author = new Author();
        tekst.addAssociatedAuthors(author);
        assertTrue(tekst.getAssociatedAuthors().contains(author));
    }

    @Test
    void testSetOneAssociatedAuthor() {
        Author author1 = new Author();
        Author author2 = new Author();
        tekst.addAssociatedAuthors(author1);
        tekst.setOneAssociatedAuthors(author2);
        assertEquals(1, tekst.getAssociatedAuthors().size());
        assertTrue(tekst.getAssociatedAuthors().contains(author2));
        assertFalse(tekst.getAssociatedAuthors().contains(author1));
    }

    @Test
    void testToString() {
        String toStringResult = tekst.toString();
        assertTrue(toStringResult.contains("Sample text"));
        assertTrue(toStringResult.contains(LocalDate.EPOCH.toString()));
    }

    @Test
    void testEqualsAndHashCode() {
        Tekst tekst1 = new Tekst("Same text");
        Tekst tekst2 = new Tekst("Same text");
        Tekst tekst3 = new Tekst("Different text");

        assertEquals(tekst1, tekst2);
        assertNotEquals(tekst1, tekst3);
        assertEquals(tekst1.hashCode(), tekst2.hashCode());
        assertNotEquals(tekst1.hashCode(), tekst3.hashCode());
    }
}
