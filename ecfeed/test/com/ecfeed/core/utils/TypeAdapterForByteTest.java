package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

// ADR-REF - add tests for methods and other adapters 

public class TypeAdapterForByteTest {

	@Test
	public void shouldConvertNumericToRandomizedRange() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10", true);

		assertEquals("10:10", result);
	}

	@Test
	public void shouldConvertNumericValue() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10", false);

		assertEquals("10", result);
	}	

	@Test
	public void shouldConvertNonNumericValue() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("ABC", false);

		assertEquals("0", result);
	}	

}
