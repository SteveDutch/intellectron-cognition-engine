package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.ZettelRepository;


class ZettelServiceTest {
	
	@Mock
	private ZettelService zettelService;
	@Mock
	private ZettelRepository zettelRepo;
	
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
		Zettel sut = new Zettel(null, null, null, null);
		System.out.println(sut);
		zettelService = new ZettelService();
	
		
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
