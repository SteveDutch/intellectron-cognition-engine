package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.repository.ZettelRepository;

class ZettelServiceTest {

	@Mock
	private ZettelService zettelService;
	@Mock
	private ZettelRepository zettelRepo;

	/*
	 * Beispiel:should_do_this_and-that // Three A's // Arrange, Act, Assert
	 */

	@Test
	void testUpdateZettel() {
		fail("Not yet implemented");
	}

	@Test
	void testCreateZettel() {
		// Arrange
		ZettelDtoRecord zettelDto = new ZettelDtoRecord(null, null, null, null,
				null/* initialize with necessary values */);

		// Act
		ZettelDtoRecord result = zettelService.createZettel(zettelDto);

		// Assert
		assertNotNull(result);
		// Add more assertions as needed to verify the behavior of the createZettel
		// method
	}
}