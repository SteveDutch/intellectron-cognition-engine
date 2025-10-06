package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stevedutch.intellectron.domain.Tekst;

@ExtendWith(MockitoExtension.class)
class TextManipulationServiceTest {
	
	
	
	@InjectMocks
	private TextManipulationService textManipulationService;

	@Test
	void testReduceTextStringListElements() {
		// Arrange
		List<Tekst> tekster = new ArrayList<>();
		Tekst tekst1 = new Tekst("Test Text 1");
		Tekst tekst2 = new Tekst("Test Text 2");
		Tekst tekst3 = new Tekst("kurz");
		tekster.add(tekst1);
		tekster.add(tekst2);
		tekster.add(tekst3);

		// Act
		textManipulationService.reduceTextStringListElements(tekster, 5);

		// Assert
		assertEquals("Test ", tekster.get(0).getText());
		assertEquals("Test ", tekster.get(1).getText());
		assertEquals("kurz", tekster.get(2).getText());

	}
	@Test
	void testReduceTitleStringListElements() {
		// Arrange
		List<Tekst> tekster = new ArrayList<>();
		Tekst tekst1 = new Tekst("Test Title 1");
		Tekst tekst2 = new Tekst("Test Title 2");
		Tekst tekst3 = new Tekst("kurz");
		tekst1.setTitle("Test Titel 1");
		tekst2.setTitle("Test Titel 2");
		tekst3.setTitle("kurz");
		tekster.add(tekst1);
		tekster.add(tekst2);
		tekster.add(tekst3);

		// Act
		textManipulationService.reduceTitleStringListElements(tekster, 5);

		// Assert
		assertEquals("Test ", tekster.get(0).getTitle());
		assertEquals("Test ", tekster.get(1).getTitle());
		assertEquals("kurz", tekster.get(2).getTitle());

	}

}
