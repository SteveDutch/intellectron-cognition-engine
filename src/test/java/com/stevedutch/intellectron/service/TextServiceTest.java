package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.repository.TextRepository;
import com.stevedutch.intellectron.service.feedback.TextSavingFeedbackHolder;

@ExtendWith(MockitoExtension.class)
public class TextServiceTest {

	@Mock
	private TextRepository textRepo;

	@Mock
	private SearchService searchService;
	@Mock
	private TextFingerprintService textFingerprintService;
	@Mock
	private TextSavingFeedbackHolder feedbackHolder;

	@InjectMocks
	private TextService textService;

	private Tekst tekst;
	private Author author;
	private Zettel zettel;

	@BeforeEach
	public void setUp() {
		tekst = new Tekst();
		tekst.setText("testText");
		author = new Author();
		zettel = new Zettel();
	}

	@Test
	public void testSaveTextNew() {
		// Arrange
		String newTextContent = "testText";
		tekst.setText(newTextContent);
		when(textRepo.save(any(Tekst.class))).thenReturn(tekst);
		when(textFingerprintService.findExactOrSimilarText(newTextContent)).thenReturn(Optional.empty());

		// Act
		Tekst savedTekst = textService.saveText(tekst);

		// Assert
		assertEquals(tekst, savedTekst);
		verify(textFingerprintService).findExactOrSimilarText(newTextContent);
		verify(feedbackHolder).setMessage("Text saved as new entry.");
		verify(textRepo, times(1)).save(tekst);
	}

	@Test
	public void testSaveTextExisting() {
		// Arrange
		String existingTextContent = "testText";
		tekst.setText(existingTextContent);
		tekst.setTextId(123L);
		Tekst existingTextFromDb = new Tekst(existingTextContent);
		existingTextFromDb.setTextId(123L);
		when(textFingerprintService.findExactOrSimilarText(existingTextContent)).thenReturn(Optional.of(existingTextFromDb));
		when(textRepo.save(existingTextFromDb)).thenReturn(existingTextFromDb);

		// Act
		Tekst savedTekst = textService.saveText(tekst);

		// Assert
		assertEquals(existingTextFromDb, savedTekst);
		assertEquals(123L, savedTekst.getTextId());
		verify(textFingerprintService).findExactOrSimilarText(existingTextContent);
		verify(feedbackHolder).setMessage("Text saved as existing entry.");
		verify(textRepo, times(1)).save(existingTextFromDb);
	}

	@Test
	public void testSaveTextWithAuthor() {
		// Arrange
		when(textRepo.save(any(Tekst.class))).thenReturn(tekst);
		List<Author> authors = new ArrayList<>();
		authors.add(author);

		// Act
		Tekst savedTekst = textService.saveTextWithAuthor(tekst, author);

		// Assert
		assertEquals(authors, savedTekst.getAssociatedAuthors());
		verify(textRepo, times(1)).save(tekst);
	}

	@Test
	public void testSaveTextWithZettel() {
		// Arrange
		when(textRepo.findByText(anyString())).thenReturn(null);
		when(textRepo.save(any(Tekst.class))).thenReturn(tekst);

		// Act
		Tekst savedTekst = textService.saveTextwithZettel(tekst, zettel);

		// Assert
		assertEquals(1, savedTekst.getZettels().size());
		verify(textRepo, times(1)).save(tekst);
	}

	@Test
	public void testUpdateTekst() {
		// Arrange
		Long zettelId = 123L;
		Tekst tekst = new Tekst("Sample text");
		Zettel zettel = new Zettel();
		Tekst updatedTekst = new Tekst("Updated text");

		when(searchService.findZettelById(zettelId)).thenReturn(zettel);
		when(searchService.findByText(tekst.getText())).thenReturn(null);
		when(textRepo.save(any(Tekst.class))).thenReturn(updatedTekst);

		// Act
		Tekst result = textService.updateTekst(zettelId, tekst);

		// Assert
		verify(searchService).findZettelById(zettelId);
		verify(searchService).findByText(tekst.getText());

		// Additional assertions can be added based on the expected behavior of the
		// method
	}

	@Test
	public void testCheckForExistingTekstExisting() {
		// Arrange
		when(textRepo.findByText(anyString())).thenReturn(tekst);

		// Act
		Tekst existingTekst = textService.checkForExistingTekst(tekst);

		// Assert
		assertEquals(tekst, existingTekst);
	}

	@Test
	public void testCheckForExistingTekstNew() {
		// Arrange
		when(textRepo.findByText(anyString())).thenReturn(null);

		// Act
		Tekst newTekst = textService.checkForExistingTekst(tekst);

		// Assert
		assertEquals(tekst, newTekst);
	}

	@Test
	void testUpdateTekst_existingTekst() {
		// Arrange
		Long zettelId = 1L;
		Tekst tekst = new Tekst("Test Text");
		Zettel zettel = new Zettel();
		Tekst existingTekst = new Tekst("Test Text");

		when(searchService.findZettelById(zettelId)).thenReturn(zettel);
		when(searchService.findByText(tekst.getText())).thenReturn(existingTekst);
		when(textRepo.save(any(Tekst.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		Tekst updatedTekst = textService.updateTekst(zettelId, tekst);

		// Assert
		assertEquals(tekst.getTitle(), updatedTekst.getTitle());
		assertEquals(tekst.getTextDate(), updatedTekst.getTextDate());
		assertEquals(tekst.getSource(), updatedTekst.getSource());
		assertEquals(tekst.getText().strip(), updatedTekst.getText());
		verify(textRepo, atLeast(1)).findByText(tekst.getText());
		assertEquals(updatedTekst, zettel.getTekst());
	}

	@Test
	public void testUpdateTekst_newTekst() {
		// Arrange
		Long zettelId = 1L;
		Tekst tekst = new Tekst("New Text");
		Zettel zettel = new Zettel();

		when(searchService.findZettelById(zettelId)).thenReturn(zettel);
		when(searchService.findByText(tekst.getText())).thenReturn(null);
		when(textRepo.save(any(Tekst.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		Tekst updatedTekst = textService.updateTekst(zettelId, tekst);

		// Assert
		assertEquals(tekst.getTitle(), updatedTekst.getTitle());
		assertEquals(tekst.getTextDate(), updatedTekst.getTextDate());
		assertEquals(tekst.getSource(), updatedTekst.getSource());
		assertEquals(tekst.getText().strip(), updatedTekst.getText());
		verify(textRepo, atLeast(1)).findByText(tekst.getText());
		assertEquals(updatedTekst, zettel.getTekst());
	}

}
