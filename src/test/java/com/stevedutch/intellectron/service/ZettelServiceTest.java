package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Zettel;


class ZettelServiceTest {
	
	@Autowired
	private ZettelService zettelService;
	
	/*
	 * Tests for boolean add (int index, T item) throws IndexOutOfBoundsException;
	 * Beispiel:should_do_this_and-that
	 * 		// Three A's
		// Arrange, Act, Assert
	 */

// TO fix not working unit test
	@Test
	void testSaveZettel() {
		//Arrange
		Zettel sut = new Zettel(null, "Test Topic oder Titel", new Note("Grandioser Gedanke, genius note"), 
				LocalDate.now(), LocalDate.now(), null, null, null, null
				/* LocalDanullte added, LocalDate changed, Integer signature,
				List<Author> authors, List<ZettelTag> zettelTags, List<Tekst> teksts*/);
		System.out.println(sut);
	
		
		//Act
		zettelService.saveZettel(sut);
		System.out.println(sut.getZettelId());
		//Assert
		assertNotNull(sut.getZettelId());
	}

	@Test
	void testUpdateZettel() {
		fail("Not yet implemented");
	}

}
