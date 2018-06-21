package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TypeAdapterForCharTest {

	@Test
	public void shouldConvertNumericToRandomizedRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("10", true);
		assertEquals("0:0", result);

		result = typeAdapterForChar.convert("10.0", true);
		assertEquals("0:0", result);
	}

	@Test
	public void shouldConvertNonNumericShortToRandomizedRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("X", true);

		assertEquals("X:X", result);
	}


	@Test
	public void shouldConvertNumericValue() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("10", false);

		assertEquals("0", result);
	}	

	@Test
	public void shouldConvertNonNumericShortValue() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("A", false);

		assertEquals("A", result);
	}	

	@Test
	public void shouldConvertNonNumericLongValue() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("ABC", false);

		assertEquals("0", result);
	}	

}
