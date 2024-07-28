package com.stevedutch.intellectron.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.exception.SearchTermNotFoundException;
import com.stevedutch.intellectron.repository.AuthorRepository;
import com.stevedutch.intellectron.repository.TagRepository;
import com.stevedutch.intellectron.repository.ZettelRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

	@InjectMocks
	private SearchService searchService;

	@Mock
	private ZettelRepository zettelRepo;

	@Mock
	private AuthorRepository authorRepo;

	@Mock
	private TagRepository tagRepo;

	@BeforeEach
	void setUp() {
		// In JUnit 5 mit Mockito-Extension ist keine explizite Initialisierung nötig
	}

	@Test
	void testFindZettelByNoteFragment() {
		String noteFragment = "test";
		List<Zettel> expectedZettels = Arrays.asList(new Zettel(), new Zettel());
		when(zettelRepo.findZettelByNoteFragment(noteFragment)).thenReturn(expectedZettels);

		List<Zettel> result = searchService.findZettelByNoteFragment(noteFragment);

		assertEquals(expectedZettels, result);
		verify(zettelRepo).findZettelByNoteFragment(noteFragment);
	}

	@Test
	void testFindZettelByNoteFragmentWithEmptyString() {
		assertThrows(SearchTermNotFoundException.class, () -> {
			searchService.findZettelByNoteFragment("");
		});
	}

	@Test
	void testFindZettelByTextFragment_Found() {
		Zettel zettel = new Zettel();
		zettel.setZettelId(1L);
		zettel.setTopic("Beispieltext");

		when(zettelRepo.findZettelByTextFragment(anyString())).thenReturn(Arrays.asList(zettel));

		List<Zettel> result = searchService.findZettelByTextFragment("Beispieltext");

		assertNotNull(result);
		assertEquals(1, result.size());
		verify(zettelRepo, times(1)).findZettelByTextFragment("Beispieltext");
	}

	@Test
	void testFindZettelByTextFragment_NotFound() {
		when(zettelRepo.findZettelByTextFragment(anyString())).thenReturn(Arrays.asList());

		List<Zettel> result = searchService.findZettelByTextFragment("Nonexistent text");

		assertTrue(result.isEmpty());
		verify(zettelRepo, times(1)).findZettelByTextFragment("Nonexistent text");
	}

	@Test
	void testFindZettelByTextFragment_InvalidInput() {
		assertThrows(SearchTermNotFoundException.class, () -> searchService.findZettelByTextFragment(null));
	}

	@Test
	void testfindOneZettelByNote_MatchFound() {
		// Arrange
		Zettel testZettel1 = new Zettel();
		String noteText = "Test Note 1";
		when(zettelRepo.findOneZettelByNote(noteText)).thenReturn(testZettel1);

		// Act
		Zettel result = searchService.findOneZettelByNote(noteText);

		// Assert
		assertEquals(testZettel1, result);
	}

	@Test
	void testfindOneZettelByNote_shouldReturnExpectedZettel_whenMultipleZettelsHaveSameNote() {
		// Arrange
		String noteText = "test note";
		Note sutNote = new Note();
		sutNote.setNoteText(noteText);
		Zettel expectedZettel = new Zettel();
		expectedZettel.setNote(sutNote);

		when(zettelRepo.findOneZettelByNote(noteText)).thenReturn(expectedZettel);

		// Act
		Zettel result = searchService.findOneZettelByNote(noteText);

		// Assert
		assertNotNull(result, "Returned Zettel should not be null");
		assertEquals(expectedZettel, result, "Returned Zettel should match the expected one");
		assertEquals(noteText, result.getNote().getNoteText(), "Note text should match the input");
		verify(zettelRepo, times(1)).findOneZettelByNote(noteText);
		verifyNoMoreInteractions(zettelRepo);
	}

	@Test
	void testfindOneZettelByNote_NoMatchFound() {
		// Given
		String noteText = "Nonexistent Note";
		when(zettelRepo.findOneZettelByNote(noteText)).thenReturn(null);

		// When
		Zettel result = searchService.findOneZettelByNote(noteText);

		// Then
		assertNull(result);
	}

	@Test
	void testfindOneZettelByNote_EmptyNoteText() {
		// Arrange
		String noteText = "";

		// act & assert
		assertThrows(SearchTermNotFoundException.class, () -> searchService.findOneZettelByNote(noteText));
		SearchTermNotFoundException exception = assertThrows(SearchTermNotFoundException.class,
				() -> searchService.findOneZettelByNote(noteText));
		assertEquals("no search term provided", exception.getMessage());

	}

	@Test
	void testfindOneZettelByNote_NullNoteText() {
		// arrange
		String noteText = null;

		// act & assert
		assertThrows(SearchTermNotFoundException.class, () -> searchService.findOneZettelByNote(noteText));
		SearchTermNotFoundException exception = assertThrows(SearchTermNotFoundException.class,
				() -> searchService.findOneZettelByNote(noteText));
		assertEquals("no search term provided", exception.getMessage());

	}

	@Test
	void testFindAllZettelWithTopic_ReturnsNonEmptyList() {
		// Arrange
		List<Zettel> expectedZettels = Arrays.asList(new Zettel(), new Zettel());
		when(zettelRepo.findAllZettelWithTopic()).thenReturn(expectedZettels);

		// Act
		List<Zettel> result = searchService.findAllZettelWithTopic();

		// Assert
		assertNotNull(result);
		assertEquals(expectedZettels.size(), result.size());
	}

	@Test
	void testFindAllZettelWithTopic_ReturnsEmptyList() {
		// Arrange
		when(zettelRepo.findAllZettelWithTopic()).thenReturn(Arrays.asList());

		// Act
		List<Zettel> result = searchService.findAllZettelWithTopic();

		// Assert
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	void testFindAllZettelWithTopic_RepositoryMethodCalled() {
		// Arrange
		List<Zettel> expectedZettels = Arrays.asList(new Zettel(), new Zettel());
		when(zettelRepo.findAllZettelWithTopic()).thenReturn(expectedZettels);

		// Act
		List<Zettel> result = searchService.findAllZettelWithTopic();

		// Assert
		assertEquals(expectedZettels, result);
	}

	@Test
	void testFindAllZettelWithTopic_RepositoryReturnsNull() {
		// Arrange
		when(zettelRepo.findAllZettelWithTopic()).thenReturn(null);

		// Act
		List<Zettel> result = searchService.findAllZettelWithTopic();

		// Assert
		assertEquals(null, result);
	}

	@Test
	void testFindAllZettelWithTopic_RepositoryReturnsSingleElement() {
		// Arrange
		Zettel singleZettel = new Zettel();
		List<Zettel> expectedZettels = Arrays.asList(singleZettel);
		when(zettelRepo.findAllZettelWithTopic()).thenReturn(expectedZettels);

		// Act
		List<Zettel> result = searchService.findAllZettelWithTopic();

		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(singleZettel, result.get(0));
	}

	@Test
	void testFindZettelById() {
		Long zettelId = 1L;
		Zettel expectedZettel = new Zettel();
		when(zettelRepo.findById(zettelId)).thenReturn(Optional.of(expectedZettel));

		Zettel result = searchService.findZettelById(zettelId);

		assertEquals(expectedZettel, result);
	}

	@Test
	void testFindZettelById_NotFound() {
		when(zettelRepo.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> searchService.findZettelById(1L));
	}

	@Test
	void testFindZettelById_NullId() {
		assertThrows(EntityNotFoundException.class, () -> searchService.findZettelById(null));
	}

	@Test
	void testFindZettelById_NegativeId() {
		assertThrows(EntityNotFoundException.class, () -> searchService.findZettelById(-1L));
	}

	@Test
	void testFindZettelByTopicFragment_ValidFragment() {
		String topicFragment = "test";
		List<Zettel> expectedZettels = Arrays.asList(new Zettel(), new Zettel());
		when(zettelRepo.findZettelByTopicFragment(topicFragment)).thenReturn(expectedZettels);

		List<Zettel> result = searchService.findZettelByTopicFragment(topicFragment);

		assertNotNull(result);
		assertEquals(expectedZettels, result);
		verify(zettelRepo, times(1)).findZettelByTopicFragment(topicFragment);
	}

	@Test
	void testFindZettelByTopicFragment_EmptyFragment() {
		assertThrows(SearchTermNotFoundException.class, () -> {
			searchService.findZettelByTopicFragment("");
		});
	}

	@Test
	void testFindZettelByTopicFragment_NullFragment() {
		assertThrows(SearchTermNotFoundException.class, () -> {
			searchService.findZettelByTopicFragment(null);
		});
	}

	@Test
	void testFindZettelByTopicFragment_NoResults() {
		String topicFragment = "nonexistent";
		when(zettelRepo.findZettelByTopicFragment(topicFragment)).thenReturn(Arrays.asList());

		List<Zettel> result = searchService.findZettelByTopicFragment(topicFragment);

		assertNotNull(result);
		assertEquals(0, result.size());
		verify(zettelRepo, times(1)).findZettelByTopicFragment(topicFragment);
	}

	@Test
	void testFindZettelByTopicFragment_ExceptionHandling() {
		String topicFragment = "error";
		when(zettelRepo.findZettelByTopicFragment(topicFragment)).thenThrow(new RuntimeException("Database error"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			searchService.findZettelByTopicFragment(topicFragment);
		});

		assertEquals("Database error", exception.getMessage());
		verify(zettelRepo, times(1)).findZettelByTopicFragment(topicFragment);
	}

	@Test
	void testFindZettelByTag_ReturnsEmptyList_WhenNoZettelAssociated() {
		// Arrange
		Tag testTag = new Tag();
		testTag.setTagText("testTag");

		when(tagRepo.findByTagText("testTag")).thenReturn(java.util.Optional.of(testTag));
		when(zettelRepo.findZettelByTags(testTag)).thenReturn(Collections.emptyList());

		// Act
		List<Zettel> result = searchService.findZettelByTag("testTag");

		// Assert
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void testFindZettelByTag_ReturnsEmptyList_WhenTagNotFound() {
		// Arrange
		when(tagRepo.findByTagText("nonexistentTag"))
				.thenThrow(new SearchTermNotFoundException("No Tag found with text: nonexistentTag"));

		// Act & Assert
		try {
			searchService.findZettelByTag("nonexistentTag");
		} catch (SearchTermNotFoundException e) {
			assertEquals("No Tag found with text: nonexistentTag", e.getMessage());
		}
	}

	@Test
	void testFindZettelByTag_ReturnsEmptyList_WhenTagTextIsEmpty() {
		// Act & Assert
		try {
			searchService.findZettelByTag("");
		} catch (SearchTermNotFoundException e) {
			assertEquals("No Tag found with text: ", e.getMessage());
		}
	}

	@Test
	void testFindZettelByTag_ReturnsEmptyList_WhenTagTextIsNull() {
		// Act & Assert
		try {
			searchService.findZettelByTag(null);
		} catch (SearchTermNotFoundException e) {
			assertEquals("No Tag found with text: null", e.getMessage());
		}
	}

	@Test
	void testFindZettelByTag_ReturnsEmptyList_WhenTagHasNoAssociatedZettel() {
		// Arrange
		Tag testTag = new Tag();
		when(tagRepo.findByTagText("testTag")).thenReturn(java.util.Optional.of(testTag));
		when(zettelRepo.findZettelByTags(testTag)).thenReturn(Collections.emptyList());

		// Act
		List<Zettel> result = searchService.findZettelByTag("testTag");

		// Assert
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void testFindTagByTagFragment_CommonPrefix() {
		// Arrange
		String tagFragment = "com";
		List<Tag> expectedTags = Arrays.asList(new Tag("common"), new Tag("community"), new Tag("compact"));
		when(tagRepo.findByTagFragment(tagFragment)).thenReturn(expectedTags);

		// Act & Assert
		assertDoesNotThrow(() -> {
			List<Tag> result = searchService.findTagByTagFragment(tagFragment);
			assertEquals(expectedTags, result);
		});
	}

	@Test
	void testFindTagByTagFragment_SingleMatch() {
		// Arrange
		String tagFragment = "uni";
		List<Tag> expectedTags = Arrays.asList(new Tag("unique"));
		when(tagRepo.findByTagFragment(tagFragment)).thenReturn(expectedTags);

		// Act & Assert
		assertDoesNotThrow(() -> {
			List<Tag> result = searchService.findTagByTagFragment(tagFragment);
			assertEquals(expectedTags, result);
		});
	}

	@Test
	void testFindTagByTagFragment_EmptyResult() {
		// Arrange
		String tagFragment = "xyz";
		when(tagRepo.findByTagFragment(tagFragment)).thenReturn(Arrays.asList());
		// Act & Assert
		assertThrows(SearchTermNotFoundException.class, () -> {
			searchService.findTagByTagFragment(tagFragment);
		});
	}

	@Test
	void testFindAuthorByName() {
		String authorName = "John Doe";
		List<Author> expectedAuthors = Arrays.asList(new Author(), new Author());
		when(authorRepo.findByAuthorFamilyNameLike("Doe")).thenReturn(expectedAuthors);

		List<Author> result = searchService.findAuthorByName(authorName);

		assertEquals(expectedAuthors, result);
		verify(authorRepo).findByAuthorFamilyNameLike("Doe");
	}

	@Test
	void testValidateSearchString_NullInput() {
		assertThrows(SearchTermNotFoundException.class, () -> {
			searchService.validateSearchString(null);
		});
	}

	@Test
	void testValidateSearchString_EmptyInput() {
		assertThrows(SearchTermNotFoundException.class, () -> {
			searchService.validateSearchString("");
		});
	}

	@Test
	void testValidateSearchString_ValidInput() {
		// This should not throw any exception
		searchService.validateSearchString("DAS HIER IST VALIDE");
	}

	@Test
	void testValidateSearchString_WhitespaceInput() {
		assertThrows(SearchTermNotFoundException.class, () -> {
			searchService.validateSearchString("   ");
		});
	}

	@Test
	void testValidateSearchString_SpecialCharactersInput() {

		assertDoesNotThrow(() -> {
			searchService.validateSearchString("!@#$%^&*()");
		});
	}

	@Test
	void testFind10RandomZettel() {
		Zettel randomZettel = new Zettel();
		when(zettelRepo.findOneRandomZettel()).thenReturn(randomZettel);

		List<Zettel> result = searchService.find10RandomZettel();

		assertEquals(10, result.size());
		verify(zettelRepo, times(10)).findOneRandomZettel();
	}
}
