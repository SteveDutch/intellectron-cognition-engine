package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InputControllerTest {

	@Test
	void testShowInputMask() {
		InputController sut = new InputController();
		
		String result = sut.showInputMask(null);
		
		assertThat(result).isEqualTo("/input");
	}

}
