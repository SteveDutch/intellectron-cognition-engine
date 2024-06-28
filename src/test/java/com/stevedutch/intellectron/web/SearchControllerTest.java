package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;

import com.stevedutch.intellectron.domain.Author;
import com.stevedutch.intellectron.domain.Note;
import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.domain.Zettel;
import com.stevedutch.intellectron.service.SearchService;
import com.stevedutch.intellectron.service.TagService;
import com.stevedutch.intellectron.service.ZettelService;

@ExtendWith(MockitoExtension.class)
public class SearchControllerTest {

    @InjectMocks
    private SearchController searchController;
    
    @Mock
    private SearchService searchService;
    
    @Mock
    private TagService tagService;
    
    @Mock
    private ZettelService zettelService;

    @Mock
    private ModelMap model;

    @BeforeEach
    void setUp() {
        // Initialisierung, falls benötigt
    	 MockitoAnnotations.openMocks(this);
    	   ZettelService zettelService = mock(ZettelService.class);
    }

    @Test
    public void testShowSearchPage() {
        String result = searchController.showSearchPage();

        assertThat(result).isEqualTo("/search");
        
    }

    // The 'searchByTag' method logs the tagText parameter and retrieves a list of zettels associated with the tagText parameter. The method then adds the wantedTag and zettels to the model and returns the string "/results".
    @Test
    public void test_search_by_tag() {
        // Arrange
 
        String tagText = "exampleTag";
        ModelMap model = new ModelMap();
        
        // Mock behavior
        Tag tag = new Tag();
        tag.setTagText(tagText);
        when(tagService.findTagByText(tagText)).thenReturn(tag);
    
        // Act
        String result = searchController.searchZettelByTag(tagText, model);
    
        // Assert
        assertEquals("/results", result);
        assertEquals(tag, model.get("tag"));
        assertNotNull(model.get("zettels"));
    }
    
    @Test
    public void testSearchTextByTextFragment() {
        String textFragment = "test";
        List<Tekst> expectedTexts = new ArrayList<>();
        Tekst tekst = new Tekst(); 
        expectedTexts.add(tekst);

        when(searchService.findTekstByTextFragment(anyString())).thenReturn(expectedTexts);

        String viewName = searchController.searchTextByTextFragment(textFragment, model);

        assertThat(viewName).isEqualTo("/results");
        verify(searchService).findTekstByTextFragment(textFragment);
        verify(model, times(1)).addAttribute(eq("textFragment"), eq(textFragment));
        verify(model, times(1)).addAttribute(eq("texts"), eq(expectedTexts));
    }
    
    @Test
    void test_search_by_topic_fragment_found() {
        // Arrange
        String topicFragment = "Testat";
        List<Zettel> expectedZettels = new ArrayList<>();
        Zettel zettel = new Zettel();  
        expectedZettels.add(zettel);
        
        when(zettelService.findZettelByTopicFragment(topicFragment)).thenReturn(expectedZettels);

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
    void testSearchTextByTextFragment_TextsFound() {
        // Arrange
        String textFragment = "example";
        List<Tekst> expectedTexts = new ArrayList<>();
        expectedTexts.add(new Tekst("Example text"));

        when(searchService.findTekstByTextFragment(anyString())).thenReturn(expectedTexts);

        // Act
        String viewName = searchController.searchTextByTextFragment(textFragment, model);

        // Assert
        assertEquals("/results", viewName);
        verify(searchService).findTekstByTextFragment(textFragment);
        verify(model, times(1)).addAttribute("textFragment", textFragment);
        verify(model, times(1)).addAttribute("texts", expectedTexts);
    }
    
    @Test
    public void testSearchAuthor_AuthorsFound() {
        // Arrange
        String lastName = "Smith";
        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(new Author("John", "Smith"));
        when(searchService.findAuthorByName(lastName)).thenReturn(expectedAuthors);

        // Act
        String viewName = searchController.searchAuthor(lastName, model);

        // Assert
        assertEquals("/authors", viewName);
        verify(searchService).findAuthorByName(lastName);
        verify(model, times(1)).addAttribute("authors", expectedAuthors);
    }
    
    @Test
    public void testSearchAuthor_NoAuthorsFound() {
        // Arrange
        String lastName = "NonExistent";
        when(searchService.findAuthorByName(lastName)).thenReturn(null);

        // Act
        String viewName = searchController.searchAuthor(lastName, model);

        // Assert
        assertEquals("/authors", viewName);
        verify(searchService).findAuthorByName(lastName);
        verify(model, times(1)).addAttribute("authors", null);
    }
}

