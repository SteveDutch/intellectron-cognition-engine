package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;

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

		
	}
}
