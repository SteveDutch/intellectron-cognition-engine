package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;


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
