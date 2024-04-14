package com.stevedutch.intellectron.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevedutch.intellectron.service.AuthorService;
import com.stevedutch.intellectron.service.NoteService;
import com.stevedutch.intellectron.service.ReferenceService;
import com.stevedutch.intellectron.service.TagService;
import com.stevedutch.intellectron.service.TextService;
import com.stevedutch.intellectron.service.ZettelService;

class ZettelControllerTest {
	@Mock
	private ZettelService zettelService;
	@Mock
	private NoteService noteService;
	@Mock
	private TextService textService;
	@Mock
    private TagService tagService;
	@Mock
	private AuthorService authorService;
	@Mock
	private ReferenceService refService;
	@InjectMocks
	private ZettelController sut = new ZettelController();

	// (Mocks initialisieren U Injektion durchführen)
	public ZettelControllerTest() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testUpdateOneZettel() throws JsonMappingException, JsonProcessingException {
		// arrange
		String jsonTestString = "{\"zettel\":\"Test; werden die Autorebnamensfelder noch bei Zettel.html angezeigt?\",\"note\":\"Test testomat\",\"tekst\":{\"text\":\"Supertext\",\"textDate\":\"\",\"source\":\"Superbrain is ma name\"},\"tags\":[\"just a tag\"],\"author\":{\"authorFirstName\":\"Karl Marx\",\"authorFamilyName\":\"Forever\"},\"references\":[]}";
		// Create an instance of the ObjectMapper class from the Jackson library
		ObjectMapper objectMapper = new ObjectMapper();
		// parse the JSON String and convert it to a JsonNode object
		JsonNode jsonTestObject = objectMapper.readTree(jsonTestString);

		// act
		String exspectedResult = sut.updateOneZettel(1L, jsonTestString);
		// assert
		assertEquals(exspectedResult, "redirect:/zettel/");

	}

	// ZettelController successfully deletes a Zettel by ID
	@Test
	public void test_delete_zettel() {
		// arrange
		Long zettelId = 1L;

		// act
		String result = sut.deleteOneZettel(zettelId);

		// assert
		assertEquals("redirect:/welcome", result);
		verify(zettelService, times(1)).deleteOneZettelbyId(zettelId);
	}

}
