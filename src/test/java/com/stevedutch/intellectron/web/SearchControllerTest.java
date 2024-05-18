package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Tekst;
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
//        SearchController searchController = new SearchController();
        String tagText = "exampleTag";
        ModelMap model = new ModelMap();
        
        // Mock behavior
        Tag tag = new Tag();
        tag.setTagText(tagText);
        when(tagService.findTagByText(tagText)).thenReturn(tag);
    
        // Act
        String result = searchController.searchByTag(tagText, model);
    
        // Assert
        assertEquals("/results", result);
        assertEquals(tag, model.get("tag"));
        assertNotNull(model.get("zettels"));
    }
    
    @Test
    public void testSearchTextByTextFragment() {
        String textFragment = "test";
        List<Tekst> expectedTexts = new ArrayList<>();
        Tekst tekst = new Tekst(); // Erstellen Sie hier eine Instanz Ihrer Tekst-Klasse
        expectedTexts.add(tekst);

        when(searchService.findTekstByTextFragment(anyString())).thenReturn(expectedTexts);

        String viewName = searchController.searchTextByTextFragment(textFragment, model);

        assertThat(viewName).isEqualTo("/results");
        verify(searchService).findTekstByTextFragment(textFragment);
        verify(model, times(1)).addAttribute(eq("textFragment"), eq(textFragment));
        verify(model, times(1)).addAttribute(eq("texts"), eq(expectedTexts));
        verify(model, times(2)).addAttribute(anyString());
    }
    
}

