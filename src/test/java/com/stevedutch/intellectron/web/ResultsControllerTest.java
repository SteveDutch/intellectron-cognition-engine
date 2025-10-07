package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;

@ExtendWith(MockitoExtension.class)
class ResultsControllerTest {
	
	@Mock
	private ModelMap model;

	@Test
	void testShowSearchResults() {
		ResultsController sut = new ResultsController();
		model.addAttribute("test");
		String result = sut.showSearchResults(model);
		
		assertThat(result).isEqualTo("/results");

        // Verify interactions with the mock ModelMap
        verify(model, times(1)).addAttribute(anyString());
	}
}
