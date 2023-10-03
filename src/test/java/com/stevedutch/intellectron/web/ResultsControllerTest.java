package com.stevedutch.intellectron.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResultsControllerTest {

	@Test
	void testShowSearchResults() {

			ResultsController sut = new ResultsController();
			
			String result = sut.showSearchResults();
			
			assertThat(result).isEqualTo("/results");
		}

}
