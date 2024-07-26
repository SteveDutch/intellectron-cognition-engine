package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.exception.EmptyZettelException;
import com.stevedutch.intellectron.repository.NoteRepository;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private Note testNote;
    private Zettel testZettel;

    @BeforeEach
    void setUp() {
        testNote = new Note("Test note");
        testZettel = new Zettel();
        testZettel.setZettelId(1L);
    }

    
    
    
    @Test
    void testSaveNote_NewNote() {
        when(noteRepository.findOneNoteByNoteText(anyString())).thenReturn(null);
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        Note result = noteService.saveNote(testNote);

        assertNotNull(result);
        assertEquals("Test note", result.getNoteText());
        verify(noteRepository).save(testNote);
    }

    @Test
    void testSaveNote_ExistingNote() {
        Note existingNote = new Note("Test note");
        existingNote.setZettelId(2L);

        when(noteRepository.findOneNoteByNoteText(anyString())).thenReturn(existingNote);
        when(noteRepository.save(any(Note.class))).thenReturn(existingNote);

        Note result = noteService.saveNote(testNote);

        assertNotNull(result);
        assertEquals(2L, result.getZettelId());
        verify(noteRepository).save(testNote);
    }

    @Test
    void testSaveNoteWithZettel() {
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        Note result = noteService.saveNotewithZettel(testNote, testZettel);

        assertNotNull(result);
        assertEquals(testZettel, result.getZettel());
        assertEquals(testZettel.getZettelId(), result.getZettelId());
        verify(noteRepository).save(testNote);
    }
    
	void testSaveNotewithZettel() {
		// Arrange
		Note sut = new Note();
		sut.setNoteText("supertoller Testtext");
		Zettel testZettel = new Zettel();
		testZettel.setZettelId(1234L);
		testZettel.setTopic("Testzettel");
		testZettel.setChanged(LocalDateTime.now());
		testZettel.setAdded(LocalDateTime.now());
		testZettel.setNote(sut);

		// Mock any dependencies if required
        when(noteRepository.save(sut)).thenReturn(sut);
		// Act 
				
        Note result = noteService.saveNotewithZettel(sut, testZettel);
		
		//Assert
		assertNotNull(result.getZettelId());
		assertNotNull(result.getZettel());
		assertNotNull(result.getNoteText());
		
	}



    @Test
    void testConnectNoteWithZettel_NewNote() {
        when(noteRepository.findOneNoteByNoteText(anyString())).thenReturn(null);

        Note result = noteService.connectNotewithZettel(testNote, testZettel);

        assertNotNull(result);
        assertEquals(testZettel, result.getZettel());
        assertEquals(testZettel.getZettelId(), result.getZettelId());
    }

    @Test
    void testConnectNoteWithZettel_ExistingNote() {
        when(noteRepository.findOneNoteByNoteText(anyString())).thenReturn(testNote);

        Note result = noteService.connectNotewithZettel(testNote, testZettel);

        assertNotNull(result);
        assertEquals(testZettel, result.getZettel());
        assertEquals(testZettel.getZettelId(), result.getZettelId());
    }

    @Test
    void testUpdateNote() {
        Note existingNote = new Note("Old note");
        when(noteRepository.findByZettelId(anyLong())).thenReturn(existingNote);

        noteService.updateNote(1L, testNote);

        assertEquals("Test note", existingNote.getNoteText());
        verify(noteRepository).save(existingNote);
    }

    @Test
    void testNoteEmptyOrBlankCheck_ValidNote() {
        assertDoesNotThrow(() -> noteService.noteEmptyOrBlankCheck(testNote));
    }

    @Test
    void testNoteEmptyOrBlankCheck_EmptyNote() {
        Note emptyNote = new Note("");
        assertThrows(EmptyZettelException.class, () -> noteService.noteEmptyOrBlankCheck(emptyNote));
    }

    @Test
    void testNoteEmptyOrBlankCheck_BlankNote() {
        Note blankNote = new Note("   ");
        assertThrows(EmptyZettelException.class, () -> noteService.noteEmptyOrBlankCheck(blankNote));
    }
    
    @Test
    public void testNoteEmptyOrBlankCheck_ThrowsException() {
        // Arrange
        Note emptyNote = new Note("");

        // Act & Assert
        assertThrows(EmptyZettelException.class, () -> {
            noteService.noteEmptyOrBlankCheck(emptyNote);
        });
    }

    @Test
    public void testNoteEmptyOrBlankCheck_DoesNotThrowException() {
        // Arrange
        Note validNote = new Note("Valid Note");

        // Act & Assert
        assertDoesNotThrow(() -> {
            noteService.noteEmptyOrBlankCheck(validNote);
        });
    }
}
