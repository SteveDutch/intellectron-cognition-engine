package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EntryControllerTest {

	@Test
	void testShowEntry() {
		EntryController sut = new EntryController();
		String result = sut.showEntry();
		
		assertThat(result).isEqualTo("/entry");
	}

}
