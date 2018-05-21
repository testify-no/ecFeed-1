package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TypeAdapterForFloatTest {

	@Test
	public void shouldConvertNumberToRange() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("10", true);
		assertEquals("10.0:10.0", result);

		result = typeAdapterForFloat.convert("10.0", true);
		assertEquals("10.0:10.0", result);
	}

	@Test
	public void shouldConvertNumberToTheSameValue() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("10.0", false);

		assertEquals("10.0", result);
	}

	@Test
	public void shouldConvertRangeToTheSameRange() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("10.0:20.0", true);

		assertEquals("10.0:20.0", result);
	}	

	@Test
	public void shouldConvertRangeToTheFirstNumber() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("10.0:20.0", false);

		assertEquals("10.0", result);
	}

	@Test
	public void shouldConvertAlphaToDefaultRange() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("ABC", true);

		assertEquals("0.0:0.0", result);
	}

	@Test
	public void shouldConvertAlphaToDefaultNumber() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("ABC", false);

		assertEquals("0.0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultRange() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("1.0:2.0:E", true);

		assertEquals("0.0:0.0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultNumber() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("1.0:2.0:E", false);

		assertEquals("0.0", result);
	}	

}
