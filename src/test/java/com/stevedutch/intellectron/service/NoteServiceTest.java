package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.NoteRepository;

class NoteServiceTest {
	
    @InjectMocks
    private NoteService noteServiceMock;
    @Mock
    private NoteRepository noteRepoMock;
	
    @BeforeEach
    public void setUp() {
    	 MockitoAnnotations.openMocks(this);
 
    }

	@Test
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
        when(noteRepoMock.save(sut)).thenReturn(sut);
		// Act 
				
        Note result = noteServiceMock.saveNotewithZettel(sut, testZettel);
		
		//Assert
		assertNotNull(result.getZettelId());
		assertNotNull(result.getZettel());
		assertNotNull(result.getNoteText());
		
	}

}
