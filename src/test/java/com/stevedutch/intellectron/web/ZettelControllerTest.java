package com.stevedutch.intellectron.web;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.ReferenceType;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.service.AuthorService;
import com.stevedutch.intellectron.service.NoteService;
import com.stevedutch.intellectron.service.ReferenceService;
import com.stevedutch.intellectron.service.SearchService;
import com.stevedutch.intellectron.service.TagService;
import com.stevedutch.intellectron.service.TextService;
import com.stevedutch.intellectron.service.ValidationService;
import com.stevedutch.intellectron.service.ZettelService;

@ExtendWith(MockitoExtension.class)
class ZettelControllerTest {

    @InjectMocks
    private ZettelController zettelController;

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
    @Mock
    private SearchService searchService;
    @Mock
    private ValidationService valService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(zettelController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

    }

    @Test
    void testShowZettel() throws Exception {
        // Setup
    	ArrayList<Tag> tags = new ArrayList<Tag>();
    	tags.add(new Tag("test"));
    	ArrayList<Reference> references = new ArrayList<Reference>();
    	references.add(new Reference(1L, 2L, ReferenceType.RELATES_TO, "test connection"));    	Zettel zettel = new Zettel("test"); 
    	Tekst tekst = new Tekst("test");
    	zettel.setTekst(tekst);
    	tekst.setText("test text");
        given(searchService.findZettelById(anyLong())).willReturn(zettel);

        // Execute
        mockMvc.perform(get("/zettel/1"))
              .andExpect(status().isOk())
              .andExpect(view().name("/zettel"))
              .andExpect(model().attributeExists("zettel"));
    }

    @Test
    void testUpdateOneZettel() throws Exception {
        // Arrange
    	ArrayList<Tag> tags = new ArrayList<Tag>();
    	tags.add(new Tag("test"));
    	ArrayList<Reference> references = new ArrayList<Reference>();
    	references.add(new Reference(1L, 2L, ReferenceType.RELATES_TO, "test connection"));    	Zettel zettel = new Zettel("test"); 
    	Tekst tekst = new Tekst("test");
    	Note note = new Note("test");
    	Author author = new Author("test", "family");

        ZettelDtoRecord changes = new ZettelDtoRecord(zettel, tekst, note, author,tags, references); // Initialize with necessary properties"
        String json = objectMapper.writeValueAsString(changes);

        // Act & Assert
        mockMvc.perform(post("/zettel/1").content(json).contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().is3xxRedirection())
              .andExpect(redirectedUrl("/zettel/1"));
    }

    @Test
    void testDeleteOneZettel() throws Exception {
        // Execute
        mockMvc.perform(post("/zettel/1/delete"))
              .andExpect(status().is3xxRedirection())
              .andExpect(redirectedUrl("/index"));
    }
}
