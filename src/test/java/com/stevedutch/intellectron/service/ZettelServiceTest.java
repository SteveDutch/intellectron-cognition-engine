package com.stevedutch.intellectron.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.exception.EmptyZettelException;
import com.stevedutch.intellectron.exception.TopicTooLongException;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.repository.ZettelRepository;

@ExtendWith(MockitoExtension.class)
class ZettelServiceTest {

	@InjectMocks
	private ZettelService zettelService;

	@Mock
	private ZettelRepository zettelRepo;
	@Mock
	private TagService tagService;
	@Mock
	private NoteService noteService;
	@Mock
	private TextService textService;
	@Mock
	private AuthorService authorService;
	@Mock
	private ReferenceService refService;
	@Mock
	private SearchService searchService;

	private Zettel existingZettel;
	private Note existingNote;
	private Tekst existingTekst;
	private Reference existingReference;
	
	    private ZettelDtoRecord zettelDto;

	@BeforeEach
	void setUp() {
		existingNote = new Note("Existing Note");
		existingTekst = new Tekst("Existing Text");
		existingReference = new Reference("1234567890");
		existingZettel = new Zettel("Existing Zettel");
		existingZettel.setNote(existingNote);
		existingZettel.setTekst(existingTekst);
		existingZettel.getReferences().add(existingReference);
		
	}

	@Test
	void testCreateZettelWithExistingReference() {
		// Setup
		Reference testReference = new Reference();
		ArrayList<Reference> references = new ArrayList<Reference>();
		references.add(testReference);
		ZettelDtoRecord zettelDto = new ZettelDtoRecord(new Zettel("New Zettel"), new Tekst("New Text"),
				new Note("New Note"), new Author("New", "Author"), new ArrayList<>(), references);

		// Execute
		ZettelDtoRecord result = zettelService.createZettel(zettelDto);

		// Verify
		assertThat(result.zettel().getTopic()).isEqualTo("New Zettel");
	}

	@Test
	void testCreateZettelWithNewReference() {
		// Setup
		Reference newReference = new Reference("0987654321");
		ArrayList<Reference> references = new ArrayList<Reference>();
		references.add(newReference);
		ZettelDtoRecord zettelDto = new ZettelDtoRecord(new Zettel("New Zettel"), new Tekst("New Text"),
				new Note("New Note"), new Author("New", "Author"), new ArrayList<>(), references);

		// Execute
		ZettelDtoRecord result = zettelService.createZettel(zettelDto);

		// Verify
		assertThat(result.zettel().getTopic()).isEqualTo("New Zettel");
		assertThat(result.references()).containsExactly(newReference);
		verify(zettelRepo).save(any(Zettel.class));
	}

	@Test
	void testCreateZettelWithNoReferences() {
		// Setup
		ZettelDtoRecord zettelDto = new ZettelDtoRecord(new Zettel("New Zettel"), new Tekst("New Text"),
				new Note(), new Author("New", "Author"), new ArrayList<>(), new ArrayList<>());

		// Execute
		ZettelDtoRecord result = zettelService.createZettel(zettelDto);
		// Verify
		assertNotNull(result.zettel());
		assertNotNull(result.tekst());
		assertThat(result.references()).isEmpty();
		verify(zettelRepo).save(any(Zettel.class));
	}

	@Test
	void testCreateZettelWithEmptyReferences() {
		// Setup
		ZettelDtoRecord zettelDto = new ZettelDtoRecord(new Zettel("New Zettel"), new Tekst("New Text"),
				new Note("New Note"), new Author("New", "Author"), new ArrayList<>(), new ArrayList<>());

		// Execute
		ZettelDtoRecord result = zettelService.createZettel(zettelDto);

		// Verify
		assertThat(result.zettel().getTopic()).isEqualTo("New Zettel");
		assertThat(result.references()).isEmpty();
		verify(zettelRepo).save(any(Zettel.class));
	}

	@Test
	void testTopicEmptyOrBlankCheck_NonEmptyNonBlankTopic() {
		// Arrange
		Zettel zettel = new Zettel("Test Topic");
		zettelService = new ZettelService();

		// Act and Assert (no exception should be thrown)
		zettelService.topicEmptyOrBlankCheck(zettel);
	}

	@Test
	void testTopicEmptyOrBlankCheck_EmptyTopic() {
		// Arrange
		Zettel zettel = new Zettel("");
		zettelService = new ZettelService();

		// Act and Assert (EmptyZettelException should be thrown)
		assertThrows(EmptyZettelException.class, () -> {
			zettelService.topicEmptyOrBlankCheck(zettel);
		});
	}

	@Test
	void testTopicEmptyOrBlankCheck_BlankTopic() {
		// Arrange
		Zettel zettel = new Zettel("   "); // Spaces are considered blank
		zettelService = new ZettelService();

		// Act and Assert (EmptyZettelException should be thrown)
		assertThrows(EmptyZettelException.class, () -> {
			zettelService.topicEmptyOrBlankCheck(zettel);
		});
	}

	@Test
	void testTopicEmptyOrBlankCheck_NullTopic() {
		// Arrange
		Zettel zettel = new Zettel();
		zettelService = new ZettelService();

		// Act and Assert (EmptyZettelException should be thrown)
		assertThrows(EmptyZettelException.class, () -> {
			zettelService.topicEmptyOrBlankCheck(zettel);
		});
	}

	

	@Test
	public void checkTopicLength_TopicTooLong_ThrowsTopicTooLongException() {
		String tooLongTopic = "is topic is way too long, "
				+ "it exceeds the maximum length of 255 characters.is topic is "
				+ "way too long, it exceeds the maximum length of 255 characters.is"
				+ " topic is way too long, it exceeds the maximum length of 255"
				+ " characters.is topic is way too long, it exceeds the maximum "
				+ "length of 255 characters.";
	    Object maxLength = ReflectionTestUtils.getField(ZettelService.class, "TOPIC_MAX_LENGTH");
		ZettelService zettelService = new ZettelService(); // Instantiate the service class

		try {
			zettelService.checkTopicLength(tooLongTopic);
			fail("Expected TopicTooLongException to be thrown.");
		} catch (TopicTooLongException e) {
			assertEquals("this zettel's topic is too long. Maximum allowed length is " + maxLength, e.getMessage());
		}

	}

	@Test
	public void checkTopicLength_TopicAtMaxLength_DoesNotThrowException() {
		String atMaxLengthTopic = "This topic is exactly at the maximum length of 255 characters.";
		ZettelService zettelService = new ZettelService(); // Instantiate the service class

		try {
			zettelService.checkTopicLength(atMaxLengthTopic);
		} catch (TopicTooLongException e) {
			fail("Unexpected TopicTooLongException was thrown.");
		}
	}
	
	@Test
    void testSetupZettelSetsTopic() {
        zettelDto = new ZettelDtoRecord(existingZettel, existingTekst, existingNote, null, new ArrayList<>(), new ArrayList<>());

		Zettel result = zettelService.setupZettel(existingZettel, zettelDto, existingNote, 
        		existingTekst);
        assertThat(result.getTopic()).isEqualTo("Existing Zettel");
    }

    @Test
    void testSetupZettelSetsTags() {
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(new Tag("Test Tag"));
        zettelDto = new ZettelDtoRecord(existingZettel, existingTekst, existingNote, null, tags, new ArrayList<>());
        Zettel result = zettelService.setupZettel(existingZettel, zettelDto, existingNote, existingTekst);
        assertThat(result.getTags()).isEqualTo(tags);
    }

    @Test
    void testSetupZettelSetsNote() {
        zettelDto = new ZettelDtoRecord(existingZettel, existingTekst, existingNote, null, new ArrayList<>(), new ArrayList<>());

        Zettel result = zettelService.setupZettel(existingZettel, zettelDto, existingNote, existingTekst);
        assertThat(result.getNote()).isEqualTo(existingNote);
    }

    @Test
    void testSetupZettelSetsTekst() {
        zettelDto = new ZettelDtoRecord(existingZettel, existingTekst, existingNote, null, new ArrayList<>(), new ArrayList<>());

        Zettel result = zettelService.setupZettel(existingZettel, zettelDto, existingNote, existingTekst);
        assertThat(result.getTekst()).isEqualTo(existingTekst);
    }

    @Test
    void testSetupZettelSetsSignature() {
    	ZettelDtoRecord zettelDto = new ZettelDtoRecord(new Zettel("New Zettel"), new Tekst("New Text"),
				new Note("New Note"), new Author("New", "Author"), new ArrayList<>(), new ArrayList<>());

        Zettel result = zettelService.setupZettel(existingZettel, zettelDto, existingNote, existingTekst);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmddMMyyyy");
        long expectedSignature = Long.parseLong(result.getAdded().format(formatter));
        assertThat(result.getSignature()).isEqualTo(expectedSignature);
    }

    @Test
    void testSetupZettelThrowsExceptionForLongTopic() {
        String longTopic = "a".repeat(256);
        zettelDto = new ZettelDtoRecord(new Zettel(longTopic), existingTekst, existingNote, null, new ArrayList<>(), new ArrayList<>());
        assertThrows(TopicTooLongException.class, () -> {
            zettelService.setupZettel(existingZettel, zettelDto, existingNote, existingTekst);
        });
    }
    
    

}
