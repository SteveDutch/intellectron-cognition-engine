package com.stevedutch.intellectron;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.stevedutch.intellectron.service.SearchService;
import com.stevedutch.intellectron.service.ZettelService;
import com.stevedutch.intellectron.service.TextService;
import com.stevedutch.intellectron.service.TagService;
import com.stevedutch.intellectron.service.AuthorService;
import com.stevedutch.intellectron.web.HomeController;

@SpringBootTest
class IntellectronApplicationTests {

	@Autowired
	private ZettelService zettelService;
	
	@Autowired
	private SearchService searchService;
	
	@Autowired
	private TextService textService;
	
	@Autowired
	private TagService tagService;
	
	@Autowired
	private AuthorService authorService;
	
	@Autowired
	private HomeController homeController;

	@Test
	void contextLoads() {
		// Bestehender Test bleibt
	}
	
	@Test
	void allServicesAreInjected() {
		assertThat(zettelService).isNotNull();
		assertThat(searchService).isNotNull();
		assertThat(textService).isNotNull();
		assertThat(tagService).isNotNull();
		assertThat(authorService).isNotNull();
		assertThat(homeController).isNotNull();
	}
	
	@Test
	void homeControllerReturnsCorrectView() {
		Model model = new ExtendedModelMap();
		String viewName = homeController.showHomePage(model);
		
		assertThat(viewName).isEqualTo("/welcome");
		assertThat(model.containsAttribute("zettelNumber")).isTrue();
		assertThat(model.containsAttribute("textsNumber")).isTrue();
		assertThat(model.containsAttribute("tagsNumber")).isTrue();
		assertThat(model.containsAttribute("authorsNumber")).isTrue();
	}
	
	@Test
	void servicesHaveBasicFunctionality() {
		// Teste, dass die Services grundlegende Methoden haben und keine NPE werfen
		assertThat(zettelService.countAllZettel()).isNotNull();
		assertThat(textService.countAllText()).isNotNull();
		assertThat(tagService.countTags()).isNotNull();
		assertThat(authorService.countAuthors()).isNotNull();
		
		// SearchService Basisfunktionen
		assertThat(searchService.findLast10Zettel()).isNotNull();
	}
}
