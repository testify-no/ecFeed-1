package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;

public class TypeAdapterForStringTest {

	@Test
	public void shouldConvertNumericToTheSameRangeWhenRandomized() {

		TypeAdapterForString typeAdapterForString = new TypeAdapterForString();

		String result = typeAdapterForString.convert("10", true, EConversionMode.QUIET);

		assertEquals("10", result);
	}

	@Test
	public void shouldConvertNumericToTheSameRangeWhenNotRandomized() {

		TypeAdapterForString typeAdapterForString = new TypeAdapterForString();

		String result = typeAdapterForString.convert("10", false, EConversionMode.QUIET);

		assertEquals("10", result);
	}

	@Test
	public void shouldConvertNumericToTheSameRangeWhenRandomized2() {

		TypeAdapterForString typeAdapterForString= new TypeAdapterForString();

		String result = typeAdapterForString.convert("10:10", true, EConversionMode.QUIET);

		assertEquals("10:10", result);
	}

	@Test
	public void shouldConvertNumericToTheSameRangeWhenNotRandomized2() {

		TypeAdapterForString typeAdapterForString = new TypeAdapterForString();

		String result = typeAdapterForString.convert("10:10", false, EConversionMode.QUIET);

		assertEquals("10:10", result);
	}

}
