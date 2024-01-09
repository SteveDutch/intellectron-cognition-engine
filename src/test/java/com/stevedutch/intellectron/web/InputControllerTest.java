package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ModelMap;

class InputControllerTest {
	
	@Mock
	private ModelMap model;

	@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


	@Test
	void testShowInputMask() {
		InputController sut = new InputController();
		model.addAttribute("test");
		String result = sut.showInputMask(model);
		
		assertThat(result).isEqualTo("/input");
	}

}
