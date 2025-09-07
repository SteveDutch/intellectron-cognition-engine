package com.stevedutch.intellectron.domain;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class ReferenceTest {

	@Test
	void test() {
		Reference sut = new Reference();
		sut.setSourceZettelId(007L);
		 
		assertInstanceOf(Reference.class, sut);

	}

}
