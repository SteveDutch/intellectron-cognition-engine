package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.stevedutch.intellectron.domain.Tekst;
import com.stevedutch.intellectron.service.SearchService;

@ExtendWith(MockitoExtension.class)
public class SearchControllerTest {

    @InjectMocks
    private SearchController searchController;

    @Mock
    private SearchService searchService;

    @Mock
    private ModelMap model;

    @BeforeEach
    void setUp() {
        // Initialisierung, falls benötigt
    	 MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldShowSearchPage() {
        String result = searchController.showSearchPage();

        assertThat(result).isEqualTo("/search");
        
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

