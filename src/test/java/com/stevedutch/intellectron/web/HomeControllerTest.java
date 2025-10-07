package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.stevedutch.intellectron.service.AuthorService;
import com.stevedutch.intellectron.service.SearchService;
import com.stevedutch.intellectron.service.TagService;
import com.stevedutch.intellectron.service.TextManipulationService;
import com.stevedutch.intellectron.service.TextService;
import com.stevedutch.intellectron.service.ZettelService;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

	@InjectMocks
	private HomeController sut;
	
    @Mock
    private ZettelService zettelService;
    @Mock
    private TextService textService;
    @Mock
    private TagService tagService;
    @Mock
    private AuthorService authorService;
    @Mock
    private SearchService searchService;
    @Mock
    private TextManipulationService textManipulationService;
    @Mock
    private Model model;
	
	@Test
	void testShowHomePageJustCode() {
		Model model = mock(Model.class);
		when(zettelService.countAllZettel()).thenReturn(1L);
		
		String result = sut.showHomePage(model);
		
		assertThat(result).isEqualTo("/index");
	}
	
	@Test
	void testShowAlsoHomePage() {
		String result = sut.redirectToHomePage();
		
		assertThat(result).isEqualTo("redirect:/index");
	}

}
