package com.stevedutch.intellectron.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Reference;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.record.ZettelDtoRecord;
import com.stevedutch.intellectron.service.*;
import com.stevedutch.intellectron.service.feedback.TextSavingFeedbackHolder;

import java.time.LocalDate;
import java.util.Collections;

// Target only ZettelController, mocking its dependencies
@WebMvcTest(ZettelController.class)
class ZettelControllerTest {

    @Autowired
    private MockMvc mockMvc; // For performing HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // For converting DTO to JSON

    // Mock all services injected into ZettelController
    @MockBean private ZettelService zettelService;
    @MockBean private NoteService noteService;
    @MockBean private TextService textService;
    @MockBean private TagService tagService;
    @MockBean private AuthorService authorService;
    @MockBean private ReferenceService refService;
    @MockBean private SearchService searchService;
    @MockBean private ValidationService validationService;
    
    // Mock the request-scoped feedback holder
    @MockBean private TextSavingFeedbackHolder feedbackHolder;

    private ZettelDtoRecord validZettelDto;
    private Tekst resultingTekst;
    private Author validatedAuthor;
    private Long zettelId = 1L;

    @BeforeEach
    void setUp() {
        // Create a dummy Tekst object for the DTO
        Tekst inputTekstDto = new Tekst("Sample Text");
        inputTekstDto.setTitle("Sample Title");
        inputTekstDto.setTextDate(LocalDate.now());
        inputTekstDto.setSource("Sample Source");

        // Create a dummy Author for the DTO using default constructor and setters
        Author inputAuthorDto = new Author();
        inputAuthorDto.setAuthorFirstName("Test Author");
        // Assuming family name can be null or empty for this test setup
        
        // Create a dummy Zettel object needed for the DTO
        Zettel inputZettel = new Zettel();
        inputZettel.setZettelId(zettelId);
        // Set other necessary fields for Zettel if required by the controller logic

        // Create the DTO record used as input with correct signature
        validZettelDto = new ZettelDtoRecord(
                inputZettel, 
                inputTekstDto,
                null, // Note DTO (simplified for this test)
                inputAuthorDto,
                new ArrayList<Tag>(), // Correct type for Tags
                new ArrayList<Reference>() // Correct type for References
        );

        // Create the Tekst object that TextService might return
        resultingTekst = new Tekst("Sample Text");
        resultingTekst.setTextId(100L);
        resultingTekst.setHash("abc");

        // Create the Author object returned by validation
        validatedAuthor = new Author(); // Use default constructor
        validatedAuthor.setAuthorFirstName("Test Author"); // Set name
        validatedAuthor.setAuthorId(50L);
        when(validationService.ensureAuthorNames(any(Author.class))).thenReturn(validatedAuthor);
    }

    @Test
    @DisplayName("Update Zettel: Success - New Text Saved")
    void updateOneZettel_whenSuccessfulNewText_shouldRedirectAndSetFlashAttribute() throws Exception {
        // Mock TextService to return a valid Tekst object
        when(textService.updateTekst(eq(zettelId), any(Tekst.class))).thenReturn(resultingTekst);
        // Mock FeedbackHolder to contain the "new entry" message
        when(feedbackHolder.getMessage()).thenReturn("Text saved as new entry.");

        MvcResult result = mockMvc.perform(post("/zettel/{zettelId}", zettelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZettelDto)))
                .andExpect(status().is3xxRedirection()) // Expect redirect status
                .andExpect(redirectedUrl("/zettel/" + zettelId)) // Expect redirect to the correct URL
                .andExpect(flash().attributeExists("updateMessage")) // Check if flash attribute exists
                .andExpect(flash().attribute("updateMessageType", "NEW_ENTRY")) // Check message type
                .andReturn();

        String updateMessage = (String) result.getFlashMap().get("updateMessage");
        assertTrue(updateMessage.contains("Zettel updated successfully."));
        assertTrue(updateMessage.contains("Text saved as new entry."));

        // Verify services were called
        verify(textService).updateTekst(eq(zettelId), any(Tekst.class));
        verify(feedbackHolder).getMessage();
        verify(authorService).saveAuthorWithText(eq(validatedAuthor), eq(resultingTekst));
        // Verify other services as needed (noteService, tagService, etc.)
        verify(noteService).updateNote(eq(zettelId), any());
        verify(tagService).updateTags(eq(zettelId), any());
    }

    @Test
    @DisplayName("Update Zettel: Success - Similar Text Found")
    void updateOneZettel_whenSuccessfulSimilarText_shouldRedirectAndSetFlashAttribute() throws Exception {
        // Mock TextService to return a valid Tekst object
        when(textService.updateTekst(eq(zettelId), any(Tekst.class))).thenReturn(resultingTekst);
        // Mock FeedbackHolder to contain the "similar" message
        when(feedbackHolder.getMessage()).thenReturn("Text matched a similar entry found within recent texts (ID: 99, Similarity: 0.85).");

        MvcResult result = mockMvc.perform(post("/zettel/{zettelId}", zettelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZettelDto)))
              .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/zettel/" + zettelId))
                .andExpect(flash().attributeExists("updateMessage"))
                .andExpect(flash().attribute("updateMessageType", "SIMILAR_MATCH")) // Check message type
                .andReturn();

        String updateMessage = (String) result.getFlashMap().get("updateMessage");
        assertTrue(updateMessage.contains("Zettel updated successfully."));
        assertTrue(updateMessage.contains("Text matched a similar entry"));

        // Verify services were called
        verify(textService).updateTekst(eq(zettelId), any(Tekst.class));
        verify(feedbackHolder).getMessage();
        verify(authorService).saveAuthorWithText(eq(validatedAuthor), eq(resultingTekst));
    }

    @Test
    @DisplayName("Update Zettel: Text Service Throws IllegalArgumentException")
    void updateOneZettel_whenTextServiceThrowsIllegalArgument_shouldRedirectAndSetErrorMessage() throws Exception {
        // Mock TextService to throw an exception
        String exceptionMessage = "No text provided for update.";
        when(textService.updateTekst(eq(zettelId), any(Tekst.class)))
                .thenThrow(new IllegalArgumentException(exceptionMessage));
        // Mock FeedbackHolder to contain the corresponding error message (set before throwing)
        when(feedbackHolder.getMessage()).thenReturn(exceptionMessage);

        MvcResult result = mockMvc.perform(post("/zettel/{zettelId}", zettelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZettelDto)))
                .andExpect(status().is3xxRedirection()) // Still redirects
                .andExpect(redirectedUrl("/zettel/" + zettelId))
                .andExpect(flash().attributeExists("updateMessage"))
                .andExpect(flash().attribute("updateMessageType", "ERROR")) // Check message type
                .andReturn();

        String updateMessage = (String) result.getFlashMap().get("updateMessage");
        assertTrue(updateMessage.contains("Zettel updated successfully."));
        assertTrue(updateMessage.contains(exceptionMessage));

        // Verify TextService was called
        verify(textService).updateTekst(eq(zettelId), any(Tekst.class));
        // Verify feedback holder was read
        verify(feedbackHolder).getMessage();
        // Verify author association was SKIPPED because resultingTekst would be null
        verify(authorService, never()).saveAuthorWithText(any(), any());
    }

     @Test
    @DisplayName("Update Zettel: Text Service Returns Null (Simulating Error)")
    void updateOneZettel_whenTextServiceReturnsNull_shouldRedirectAndSetUnknownMessage() throws Exception {
        // Mock TextService to return null (less ideal than throwing, but testing controller robustness)
        when(textService.updateTekst(eq(zettelId), any(Tekst.class))).thenReturn(null);
        // Mock FeedbackHolder having *no* message set in this scenario (or maybe an error one)
        when(feedbackHolder.getMessage()).thenReturn(""); // Simulate no feedback set

        MvcResult result = mockMvc.perform(post("/zettel/{zettelId}", zettelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZettelDto)))
              .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/zettel/" + zettelId))
                .andExpect(flash().attributeExists("updateMessage"))
                .andExpect(flash().attribute("updateMessageType", "UNKNOWN")) // Check message type
                .andReturn();

        String updateMessage = (String) result.getFlashMap().get("updateMessage");
        assertTrue(updateMessage.contains("Zettel updated successfully."));
        assertTrue(updateMessage.contains("(Text status unknown)")); // Fallback message

        verify(textService).updateTekst(eq(zettelId), any(Tekst.class));
        verify(feedbackHolder).getMessage();
        verify(authorService, never()).saveAuthorWithText(any(), any()); // Skipped
    }
}
