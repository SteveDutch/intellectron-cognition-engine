package com.stevedutch.intellectron.unit;


import static org.assertj.core.api.Assertions.assertThat;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
 class HomeControllerTestIntegrationTest{

	@InjectMocks
	HomeControllerTest underTest;

	@Test
	 void testTestShowAlsoHomePage() throws Exception {
	// given
	// when
	underTest.testShowAlsoHomePage();
	// then
	// TODO check for expected side effect (i.e. service call, changed parameter or exception thrown)
	// verify(mock).methodcall();
	// assertThat(TestUtils.objectToJson(param)).isEqualTo(TestUtils.readTestFile("someMethod/ParamType_updated.json"));
	// assertThrows(SomeException.class, () -> underTest.someMethod());
	} 
}