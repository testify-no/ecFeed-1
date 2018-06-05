package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;

public class TypeAdapterForCharTest {

	@Test
	public void shouldConvertNumberToRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("10", true, EConversionMode.QUIET);
		assertEquals("0:0", result);

		result = typeAdapterForChar.convert("10.0", true, EConversionMode.QUIET);
		assertEquals("0:0", result);
	}

	@Test
	public void shouldConvertCharToRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("Z", true, EConversionMode.QUIET);
		assertEquals("Z:Z", result);
	}	

	@Test
	public void shouldConvertCharToTheSameValue() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("X", false, EConversionMode.QUIET);

		assertEquals("X", result);
	}

	@Test
	public void shouldConvertRangeToTheSameRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("a:A", true, EConversionMode.QUIET);

		assertEquals("a:A", result);
	}	

	@Test
	public void shouldConvertRangeToTheTheFirstNumber() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("a:A", false, EConversionMode.QUIET);

		assertEquals("a", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("abc", true, EConversionMode.QUIET);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultNumber() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("abc", false, EConversionMode.QUIET);

		assertEquals("0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultRange() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("A:B:C", true, EConversionMode.QUIET);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultNumber() {

		TypeAdapterForChar typeAdapterForChar = new TypeAdapterForChar();

		String result = typeAdapterForChar.convert("A:B:C", false, EConversionMode.QUIET);

		assertEquals("0", result);
	}	
}
