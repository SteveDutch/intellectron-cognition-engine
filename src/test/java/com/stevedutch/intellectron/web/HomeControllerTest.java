package com.stevedutch.intellectron.web;


import static org.assertj.core.api.Assertions.assertThat;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
 class HomeControllerTest{

	@InjectMocks
	HomeController underTest;

	@Test
	 void testShowHomePage() throws Exception {
	// given
	// when
	String actual=underTest.showHomePage();
	// then
	assertThat(actual).isEqualTo("TestExpected");
	} 
}