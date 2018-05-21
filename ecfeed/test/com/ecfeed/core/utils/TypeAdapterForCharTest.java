package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TypeAdapterForCharTest {

	@Test
	public void shouldConvertNumberToRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("10", true);
		assertEquals("0:0", result);

		result = typeAdapterForChar.convert("10.0", true);
		assertEquals("0:0", result);
	}

	@Test
	public void shouldConvertCharToRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("Z", true);
		assertEquals("Z:Z", result);
	}	

	@Test
	public void shouldConvertCharToTheSameValue() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("X", false);

		assertEquals("X", result);
	}

	@Test
	public void shouldConvertRangeToTheSameRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("a:A", true);

		assertEquals("a:A", result);
	}	

	@Test
	public void shouldConvertRangeToTheTheFirstNumber() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("a:A", false);

		assertEquals("a", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("abc", true);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultNumber() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("abc", false);

		assertEquals("0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("A:B:C", true);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultNumber() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("A:B:C", false);

		assertEquals("0", result);
	}	
}
