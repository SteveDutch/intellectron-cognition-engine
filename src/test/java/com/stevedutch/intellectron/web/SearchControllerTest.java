package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;

@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {

	@Mock
	private ModelMap model;

	@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



	@Test
	public void shouldShowSearchPage() {
		SearchController sut = new SearchController();
		model.addAttribute("test");
		String result = sut.showSearchPage();
		
		assertThat(result).isEqualTo("/search");

        // Verify interactions with the mock ModelMap
        verify(model, times(1)).addAttribute(anyString());
	}
}
