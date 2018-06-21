package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

// ADR-REF - add tests for methods and other adapters 

public class TypeAdapterForDoubleTest {

	@Test
	public void shouldConvertNumericToRandomizedRange() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		String result = typeAdapterForDouble.convert("10", true);

		assertEquals("10.0:10.0", result);
	}

	@Test
	public void shouldConvertNumericValue() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		String result = typeAdapterForDouble.convert("10", false);

		assertEquals("10.0", result);
	}	

	@Test
	public void shouldConvertNonNumericValue() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		String result = typeAdapterForDouble.convert("ABC", false);

		assertEquals("0.0", result);
	}	

}
