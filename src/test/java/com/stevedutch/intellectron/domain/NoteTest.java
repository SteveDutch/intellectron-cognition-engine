package com.stevedutch.intellectron.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NoteTest {
	
	private Note note;
	
	@BeforeEach
	void setUp() {
        note = new Note("nur zu Testzwecken");
            }
	

    @Test
    void testConstructorAndGetters() {
        assertEquals("nur zu Testzwecken", note.getNoteText());
        assertNull(note.getZettelId());
        assertNull(note.getZettel());
    }
    
    @Test
    void testSetters() {
        note.setZettelId(1L);
        note.setNoteText("junit is great");
        note.setZettel(new Zettel());

        assertEquals(1L, note.getZettelId());
        assertEquals("junit is great", note.getNoteText());
        assertNotNull(note.getZettel());
    }
    
    @Test
    void testToString() {
        note.setZettelId(1L);
        String expected = "Note [zettelId=1, zettel=null, noteText=nur zu Testzwecken]";
        assertEquals(expected, note.toString());
    }
    
    @Test
    void testHashCode() {
        Note sameNote = new Note("nur zu Testzwecken");
        assertEquals(note.hashCode(), sameNote.hashCode());

        Note differentNote = new Note("Japan coffee");
        assertNotEquals(note.hashCode(), differentNote.hashCode());
    }
    
    @Test
    void testEquals() {
        Note sameNote = new Note("nur zu Testzwecken");
        Note differentNote = new Note("Schmiede");

        assertTrue(note.equals(note));
        assertTrue(note.equals(sameNote));
        assertFalse(note.equals(differentNote));
        assertFalse(note.equals(null));
        assertFalse(note.equals(new Object()));
    }
    
    @Test
    void testEqualsWithDifferentFields() {
        Note noteWithId = new Note("nur zu Testzwecken");
        noteWithId.setZettelId(1L);

        assertFalse(note.equals(noteWithId));

        Note noteWithZettel = new Note("nur zu Testzwecken");
        noteWithZettel.setZettel(new Zettel());

        assertFalse(note.equals(noteWithZettel));
    }



}
