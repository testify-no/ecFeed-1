package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

// ADR-REF - add tests for methods and other adapters 

public class TypeAdapterForFloatTest {

	@Test
	public void shouldConvertNumericToRandomizedRange() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("10", true);

		assertEquals("10.0:10.0", result);
	}

	@Test
	public void shouldConvertNumericValue() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("10", false);

		assertEquals("10.0", result);
	}	

	@Test
	public void shouldConvertNonNumericValue() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("ABC", false);

		assertEquals("0.0", result);
	}	

}
