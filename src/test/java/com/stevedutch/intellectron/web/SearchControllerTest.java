package com.stevedutch.intellectron.web;

import org.aspectj.lang.annotation.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {

	private SearchController searchController;

	@Before(value = "")
	public void setup() {
		this.searchController = new SearchController();
	}



	@Test
	public void shouldShowSearchPage() {
		String actualValue = searchController.showSearchPage();

		// TODO: assert scenario

		MatcherAssert.assertThat(actualValue, Matchers.is("/entry"));
	}
}
