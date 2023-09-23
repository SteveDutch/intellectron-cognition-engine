package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


class HomeControllerTest {

	//according to DanVega, just junit
	@Test
	void testShowHomePageJustCode() {
		HomeController sut = new HomeController();
		String result = sut.showHomePage();
		
		assertThat(result).isEqualTo("/welcome");
	}
	
	@Test
	void testShowAlsoHomePage() {
		HomeController sut = new HomeController();
		String result = sut.showAlsoHomePage();
		
		assertThat(result).isEqualTo("/welcome");
	}

}
