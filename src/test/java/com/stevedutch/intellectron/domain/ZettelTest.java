package com.stevedutch.intellectron.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.stevedutch.intellectron.record.ZettelDtoRecord;

class ZettelTest {

	private Zettel zettel;
	private Note note;
	private Tekst tekst;
	private Reference reference;
	private Tag tag;
	Set<Reference> references = new HashSet<>();
	ArrayList<Reference> references2 = new ArrayList<>();
	ArrayList<Tag> tags = new ArrayList<>();

	@BeforeEach
	void setUp() {
		zettel = new Zettel("Test Topic");
		note = new Note();
		tekst = new Tekst();
		reference = new Reference();
		tag = new Tag();

		references.add(reference);

		tags.add(tag);
	}

	@Test
	void testZettelConstructor() {
		Zettel sut = new Zettel(note, tekst);

		assertNotNull(sut);
		assertEquals(note, sut.getNote());
		assertEquals(tekst, sut.getTekst());
		assertNotNull(zettel);
		assertEquals("Test Topic", zettel.getTopic());
	}

	@Test
	void testZettelConstructorFromDto() {
		// Arrange
		Zettel baseZettel = new Zettel();
		Author sutAuthor = new Author();
		LocalDateTime now = LocalDateTime.now();
		baseZettel.setZettelId(1L);
		baseZettel.setTopic("Test Topic");
		baseZettel.setNote(note);
		baseZettel.setAdded(now);
		baseZettel.setChanged(now);
		baseZettel.setReferences(references);
		baseZettel.setTags(tags);
		baseZettel.setTekst(tekst);

		// Act
		ZettelDtoRecord zettelDto = new ZettelDtoRecord(baseZettel, baseZettel.getTekst(), baseZettel.getNote(),
				sutAuthor, tags, references2);
		Zettel sut = new Zettel(zettelDto);

		// Assert
		assertEquals(baseZettel.getZettelId(), sut.getZettelId());
		assertEquals(baseZettel.getTopic(), sut.getTopic());
		assertEquals(baseZettel.getNote(), sut.getNote());
		assertEquals(baseZettel.getAdded(), sut.getAdded());
		assertEquals(baseZettel.getChanged(), sut.getChanged());
		assertEquals(baseZettel.getReferences(), sut.getReferences());
		assertEquals(baseZettel.getTags(), sut.getTags());
		assertEquals(baseZettel.getTekst(), sut.getTekst());
	}

	@Test
	void testSetAndGetZettelId() {
		zettel.setZettelId(1L);
		assertEquals(1L, zettel.getZettelId());
	}

	@Test
	void testSetAndGetTopic() {
		zettel.setTopic("New Topic");
		assertEquals("New Topic", zettel.getTopic());
	}

	@Test
	void testSetAndGetNote() {
		zettel.setNote(note);
		assertEquals(note, zettel.getNote());
	}

	@Test
	void testSetAndGetAdded() {
		LocalDateTime now = LocalDateTime.now();
		zettel.setAdded(now);
		assertEquals(now, zettel.getAdded());
	}

	@Test
	void testSetAndGetChanged() {
		LocalDateTime now = LocalDateTime.now();
		zettel.setChanged(now);
		assertEquals(now, zettel.getChanged());
	}

	@Test
	void testSetAndGetReferences() {

		zettel.setReferences(references);
		assertEquals(references, zettel.getReferences());
	}

	@Test
	void testAddReference() {
		zettel.addReference(reference);
		assertTrue(zettel.getReferences().contains(reference));
	}

	@Test
	void testSetAndGetTags() {
		List<Tag> tags = new ArrayList<>();
		tags.add(tag);
		zettel.setTags(tags);
		assertEquals(tags, zettel.getTags());
	}

	@Test
	void testAddTag() {
		zettel.addTag(tag);
		assertTrue(zettel.getTags().contains(tag));
	}

	@Test
	void testSetAndGetTekst() {
		zettel.setTekst(tekst);
		assertEquals(tekst, zettel.getTekst());
	}

	@Test
	void testEquals() {
		Zettel zettel1 = new Zettel("Topic 1");
		zettel1.setZettelId(1L);
		Zettel zettel2 = new Zettel("Topic 2");
		zettel2.setZettelId(1L);
		Zettel zettel3 = new Zettel("Topic 3");
		zettel3.setZettelId(2L);

		assertEquals(zettel1, zettel2);
		assertNotEquals(zettel1, zettel3);
	}

	@Test
	void testHashCode() {
		Zettel zettel1 = new Zettel("topic mit id I");
		zettel1.setZettelId(1L);
		Zettel zettel2 = new Zettel("topic mit id II");
		zettel2.setZettelId(2L);
		Zettel zettel3 = new Zettel("topic mit id I");
		zettel3.setZettelId(1L);

		assertNotEquals(zettel1.hashCode(), zettel2.hashCode());
		assertEquals(zettel1.hashCode(), zettel3.hashCode());
	}

	@Test
	void testToString() {
		zettel.setZettelId(1L);
		zettel.setTopic("Test Topic");
		zettel.setAdded(LocalDateTime.of(2024, 7, 25, 17, 57));
		String expectedString = "\n Zettel \n [zettelId =1, topic=Test Topic Note : No note available, it's NULL added=2024-07-25T17:57, changed=null,  Anzahl der tags=0, \n tekst=null , \n  References: []]\n \n ";
		assertEquals(expectedString, zettel.toString());
	}
}
