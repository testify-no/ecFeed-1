package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

// ADR-REF - add tests for methods and other adapters 

public class TypeAdapterForShortTest {

	@Test
	public void shouldConvertNumericToRandomizedRange() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		String result = typeAdapterForShort.convert("10", true);

		assertEquals("10:10", result);
	}

	@Test
	public void shouldConvertNumericValue() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		String result = typeAdapterForShort.convert("10", false);

		assertEquals("10", result);
	}	

	@Test
	public void shouldConvertNonNumericValue() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		String result = typeAdapterForShort.convert("ABC", false);

		assertEquals("0", result);
	}	

}
