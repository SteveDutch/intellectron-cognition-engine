package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        noteServiceMock = new NoteService(noteRepoMock);
        noteRepoMock = mock(NoteRepository.class);
    }

	@Test
	void testSaveNotewithZettel() {
		// Arrange
		Note sut = new Note();
		sut.setNoteText("supertoller Testtext");
		Zettel testZettel = new Zettel(1234L, "Das ist eine supertolle Testüberschrift", sut, LocalDateTime.now(), 
				LocalDateTime.now(), 1L ,null, null);
		

		// Mock any dependencies if required
        when(noteRepoMock.save(Mockito.any(Note.class))).thenReturn(sut);
		// Act 
		NoteService sutService = new NoteService(noteRepoMock);
		
        Note result = sutService.saveNotewithZettel(sut, testZettel);
		
		//Assert
		assertNotNull(result.getZettelId());
		assertNotNull(result.getZettel());
		assertNotNull(result.getNoteText());
		
	}

}
