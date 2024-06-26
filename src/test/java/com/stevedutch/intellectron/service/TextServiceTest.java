package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TextRepository;

@ExtendWith(MockitoExtension.class)
class TextServiceTest {
	
	@InjectMocks
	TextService textServiceMock = new TextService();
	
	@Mock
	TextRepository textRepoMock;
	
	@Test
	void testsaveTextwithZettel() {
		Tekst sut = new Tekst();
		// Arrange
		Zettel testZettel = new Zettel();
		testZettel.setZettelId(42L);
		testZettel.setAdded(LocalDateTime.now());
		testZettel.setChanged(LocalDateTime.now());
		testZettel.setSignature(null);
		testZettel.setReferences(null);
		testZettel.setTags(null);
		testZettel.setNote(null);
		testZettel.setTekst(sut);
		
		// Mock any dependencies if required
		when(textRepoMock.save(Mockito.eq(sut))).thenReturn(sut);
		
		// Act
		Tekst result = textServiceMock.saveTextwithZettel(sut, testZettel);
		
		// Assert
		assertNotNull(result);
		
	}

}
