package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;

public class TypeAdapterForShortTest {

	@Test
	public void shouldConvertNumberToRange() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		String result = typeAdapterForShort.convert("10", true, EConversionMode.QUIET);

		assertEquals("10:10", result);
	}

	@Test
	public void shouldConvertNumberToTheSameValue() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		String result = typeAdapterForShort.convert("10", false, EConversionMode.QUIET);

		assertEquals("10", result);
	}

	@Test
	public void shouldConvertRangeToTheSameRange() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		String result = typeAdapterForShort.convert("10:11", true, EConversionMode.QUIET);

		assertEquals("10:11", result);
	}	

	@Test
	public void shouldConvertRangeToTheFirstNumber() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		String result = typeAdapterForShort.convert("10:11", false, EConversionMode.QUIET);

		assertEquals("10", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultRange() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		String result = typeAdapterForShort.convert("ABC", true, EConversionMode.QUIET);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultNumber() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		String result = typeAdapterForShort.convert("ABC", false, EConversionMode.QUIET);

		assertEquals("0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultRange() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		String result = typeAdapterForShort.convert("10:11:12", true, EConversionMode.QUIET);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultNumber() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		String result = typeAdapterForShort.convert("10:11:12", false, EConversionMode.QUIET);

		assertEquals("0", result);
	}	

}
