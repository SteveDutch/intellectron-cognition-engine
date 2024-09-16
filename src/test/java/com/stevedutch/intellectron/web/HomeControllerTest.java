package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;


class HomeControllerTest {

	//according to DanVega, just junit
	@Test
	void testShowHomePageJustCode() {
		Model model = null;
		HomeController sut = new HomeController();
		String result = sut.showHomePage(model);
		
		assertThat(result).isEqualTo("/welcome");
	}
	
	@Test
	void testShowAlsoHomePage() {
		HomeController sut = new HomeController();
		String result = sut.showAlsoHomePage();
		
		assertThat(result).isEqualTo("/welcome");
	}

}
