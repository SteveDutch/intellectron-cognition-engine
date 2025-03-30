package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.service.SearchService;
import com.stevedutch.intellectron.service.TagService;
import com.stevedutch.intellectron.service.TextManipulationService;
import com.stevedutch.intellectron.service.ZettelService;
import com.stevedutch.intellectron.exception.SearchTermNotFoundException;
import com.stevedutch.intellectron.advice.GlobalExceptionHandler;
import com.stevedutch.intellectron.exception.TagNotFoundException;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {
	
    @InjectMocks
    private SearchController searchController;
    
    @Mock
    private SearchService searchService;
    
    @Mock
    private TagService tagService;
    
    @Mock
    private ZettelService zettelService;
    
    @Mock
    private TextManipulationService textManipulationService;
    
    @Mock
    private ModelMap model;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testShowSearchPage() {
        String result = searchController.showSearchPage(model);
        assertThat(result).isEqualTo("/search");
    }

    @Test
    void testSearchZettelByTagFragment_ValidTagFragment_TagsFound() {
        // Arrange
        String tagFragment = "test";
        List<Tag> expectedTags = Arrays.asList(new Tag("test"), new Tag("test2"));
        when(searchService.findTagByTagFragment(tagFragment)).thenReturn(expectedTags);

        // Act
        String viewName = searchController.searchZettelByTagFragment(tagFragment, model);

        // Assert
        assertEquals("/tags", viewName);
        verify(searchService).findTagByTagFragment(tagFragment);
        verify(model).addAttribute("wantedTags", expectedTags);
    }
    
    @Test
    void testSearchTextByTextFragment() {
        // Arrange
        String textFragment = "test";
        List<Tekst> expectedTexts = new ArrayList<>();
        Tekst tekst = new Tekst(); 
        expectedTexts.add(tekst);

        when(searchService.findTruncatedTekstByTextFragment(anyString(), eq(555))).thenReturn(expectedTexts);

        // Act
        String viewName = searchController.searchTextByTextFragment(textFragment, model);

        // Assert
        assertThat(viewName).isEqualTo("/texts");
        verify(searchService).findTruncatedTekstByTextFragment(textFragment, 555);
        verify(model, times(1)).addAttribute(eq("textFragment"), eq(textFragment));
        verify(model, times(1)).addAttribute(eq("texts"), eq(expectedTexts));
    }
    
    @Test
    void testSearchTextByTextFragment_NoTextsFound() throws Exception {
        // Arrange
        String textFragment = "nonexistent";
        when(searchService.findTruncatedTekstByTextFragment(anyString(), eq(555)))
                .thenThrow(new SearchTermNotFoundException("No Tekst found with text: " + textFragment));

        // Act & Assert
        mockMvc.perform(get("/search/text4tekst/")
                .param("textFragment", textFragment))
                .andExpect(status().isNotFound());

        verify(searchService).findTruncatedTekstByTextFragment(textFragment, 555);
    }
    
    @Test
    void test_search_by_topic_fragment_found() {
        // Arrange
        String topicFragment = "Testat";
        List<Zettel> expectedZettels = new ArrayList<>();
        Zettel zettel = new Zettel();  
        expectedZettels.add(zettel);
        
        when(searchService.findZettelByTopicFragment(topicFragment)).thenReturn(expectedZettels);

        // Act
        String viewName = searchController.searchBytopicFragment(topicFragment, model);

        // Assert
        assertEquals(viewName, "/results" );
        verify(model, times(1)).addAttribute(eq("topicFragment"), eq(topicFragment));
        verify(model, times(1)).addAttribute(eq("zettels"), eq(expectedZettels));
    }
    
    @Test
    void testsearchByNoteFragment( ) {
        // Arrange
        String noteFragment = "NoteFragment";
        List<Zettel> expectedZettels = new ArrayList<>();
        expectedZettels.add(new Zettel("TestTopic"));
        Note note = new Note();  
        
        
        when(searchService.findZettelByNoteFragment(noteFragment)).thenReturn(expectedZettels);

        // Act
        String viewName = searchController.searchByNoteFragment(noteFragment, model);

        // Assert
        assertEquals(viewName, "/results" );
        verify(model, times(1)).addAttribute(eq("noteFragment"), eq(noteFragment));
        verify(model, times(1)).addAttribute(eq("zettels"), eq(expectedZettels));
    }
    
    @Test
    void testSearchZettelByTextFragment_ZettelsFound() {
        // Arrange
        String textFragment = "test";
        List<Zettel> zettels = new ArrayList<>();
        zettels.add(new Zettel("Test Zettel"));
        ModelMap model = new ModelMap();

        when(searchService.findZettelByTextFragment(textFragment)).thenReturn(zettels);

        // Act
        String viewName = searchController.searchZettelByTextFragment(textFragment, model);

        // Assert
        assertEquals("/results", viewName);
        assertNotNull(model.get("textFragment"));
        assertEquals(textFragment, model.get("textFragment"));
        assertNotNull(model.get("zettels"));
        assertEquals(zettels, model.get("zettels"));
    }
    
    @Test
    void testSearchAuthor_AuthorsFound() {
        // Arrange
        String lastName = "Smith";
        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(new Author("John", "Smith"));
        when(searchService.findAuthorByNameWithTruncatedTexts(lastName, 555)).thenReturn(expectedAuthors);

        // Act
        String viewName = searchController.searchAuthor(lastName, model);

        // Assert
        assertEquals("/authors", viewName);
        verify(searchService).findAuthorByNameWithTruncatedTexts(lastName, 555);
        verify(model, times(1)).addAttribute("authors", expectedAuthors);
    }
    
    @Test
    void testSearchAuthor_NoAuthorsFound() throws Exception {
        // Arrange
        String lastName = "NonExistent";
        when(searchService.findAuthorByNameWithTruncatedTexts(lastName, 555))
                .thenThrow(new SearchTermNotFoundException("No Author found with name: " + lastName));

        // Act & Assert
        mockMvc.perform(get("/search/author/")
                .param("lastName", lastName))
                .andExpect(status().isNotFound());

        verify(searchService).findAuthorByNameWithTruncatedTexts(lastName, 555);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void searchZettelByTagFragment_WhenTagsFound_ShouldReturnTagsView() throws Exception {
        // Arrange
        String tagFragment = "test";
        Tag tag1 = new Tag("test1");
        Tag tag2 = new Tag("test2");
        List<Tag> expectedTags = Arrays.asList(tag1, tag2);
        
        when(searchService.findTagByTagFragment(tagFragment)).thenReturn(expectedTags);

        // Act & Assert
        mockMvc.perform(get("/search/tag/")
                .param("tagFragment", tagFragment))
                .andExpect(status().isOk())
                .andExpect(view().name("/tags"))
                .andExpect(model().attributeExists("wantedTags"))
                .andExpect(model().attributeExists("tagFragment"));

        verify(searchService).findTagByTagFragment(tagFragment);
    }

    @Test
    void searchZettelByTagFragment_WhenNoTagsFound_ShouldHandleException() throws Exception {
        // Arrange
        String tagFragment = "nonexistent";
        when(searchService.findTagByTagFragment(tagFragment))
                .thenThrow(new SearchTermNotFoundException("No tags found with fragment: " + tagFragment));

        // Act & Assert
        mockMvc.perform(get("/search/tag/")
                .param("tagFragment", tagFragment))
                .andExpect(status().isNotFound());

        verify(searchService).findTagByTagFragment(tagFragment);
    }

    @Test
    void searchZettelByTagFragment_WhenEmptyTagFragment_ShouldReturnOkWithTagsView() throws Exception {
        // Arrange
        String tagFragment = "";
        // Mock the service call for an empty fragment to return an empty list
        when(searchService.findTagByTagFragment(tagFragment)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/search/tag/")
                .param("tagFragment", tagFragment))
                .andExpect(status().isOk()) // Expect 200 OK instead of 404
                .andExpect(view().name("/tags"))
                .andExpect(model().attributeExists("wantedTags"))
                .andExpect(model().attribute("wantedTags", Arrays.asList()))
                .andExpect(model().attribute("tagFragment", tagFragment));
        
        // Verify the service was called
        verify(searchService).findTagByTagFragment(tagFragment);
    }

    @Test
    void searchZettelByTagFragment_ShouldPopulateModel() {
        // Arrange
        String tagFragment = "test";
        Tag tag1 = new Tag("test1");
        Tag tag2 = new Tag("test2");
        List<Tag> expectedTags = Arrays.asList(tag1, tag2);
        ModelMap model = new ModelMap();
        
        when(searchService.findTagByTagFragment(tagFragment)).thenReturn(expectedTags);

        // Act
        String viewName = searchController.searchZettelByTagFragment(tagFragment, model);

        // Assert
        assertEquals("/tags", viewName);
        assertEquals(expectedTags, model.get("wantedTags"));
        assertEquals(tagFragment, model.get("tagFragment"));
        verify(searchService).findTagByTagFragment(tagFragment);
    }

    @Test
    void showTagDetails_WhenTagExists_ShouldReturnTagsViewWithDetails() throws Exception {
        // Arrange
        Long tagId = 1L;
        Tag selectedTag = new Tag("Test Tag");
        selectedTag.setId(tagId);
        List<Zettel> associatedZettels = Arrays.asList(new Zettel("Zettel 1"), new Zettel("Zettel 2"));

        when(searchService.findTagById(tagId)).thenReturn(selectedTag);
        when(searchService.findZettelByTag(selectedTag.getTagText())).thenReturn(associatedZettels);

        // Act & Assert
        mockMvc.perform(get("/search/tag/{tagId}", tagId))
                .andExpect(status().isOk())
                .andExpect(view().name("/tags"))
                .andExpect(model().attributeExists("selectedTag"))
                .andExpect(model().attribute("selectedTag", selectedTag))
                .andExpect(model().attributeExists("zettels"))
                .andExpect(model().attribute("zettels", associatedZettels));

        verify(searchService).findTagById(tagId);
        verify(searchService).findZettelByTag(selectedTag.getTagText());
    }

    @Test
    void showTagDetails_WhenTagNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        Long tagId = 99L;
        when(searchService.findTagById(tagId))
                .thenThrow(new TagNotFoundException("Tag not found with id " + tagId));

        // Act & Assert
        mockMvc.perform(get("/search/tag/{tagId}", tagId))
                .andExpect(status().isNotFound());

        verify(searchService).findTagById(tagId);
        verify(searchService, never()).findZettelByTag(anyString());
    }

    @Test
    void showTagDetails_WhenTagExistsButNoZettels_ShouldReturnTagsViewWithEmptyZettelList() throws Exception {
        // Arrange
        Long tagId = 2L;
        Tag selectedTag = new Tag("Another Tag");
        selectedTag.setId(tagId);
        List<Zettel> emptyZettelList = Arrays.asList();

        when(searchService.findTagById(tagId)).thenReturn(selectedTag);
        when(searchService.findZettelByTag(selectedTag.getTagText())).thenReturn(emptyZettelList);

        // Act & Assert
        mockMvc.perform(get("/search/tag/{tagId}", tagId))
                .andExpect(status().isOk())
                .andExpect(view().name("/tags"))
                .andExpect(model().attributeExists("selectedTag"))
                .andExpect(model().attribute("selectedTag", selectedTag))
                .andExpect(model().attributeExists("zettels"))
                .andExpect(model().attribute("zettels", emptyZettelList));

        verify(searchService).findTagById(tagId);
        verify(searchService).findZettelByTag(selectedTag.getTagText());
    }
}

