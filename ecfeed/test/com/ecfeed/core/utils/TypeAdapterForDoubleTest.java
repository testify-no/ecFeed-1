package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;

public class TypeAdapterForDoubleTest {

	@Test
	public void shouldConvertNumberToRange() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		String result = typeAdapterForDouble.convert("10", true, EConversionMode.QUIET);
		assertEquals("10.0:10.0", result);

		result = typeAdapterForDouble.convert("10.0", true, EConversionMode.QUIET);
		assertEquals("10.0:10.0", result);
	}

	@Test
	public void shouldConvertNumberToTheSameValue() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		String result = typeAdapterForDouble.convert("10.0", false, EConversionMode.QUIET);

		assertEquals("10.0", result);
	}

	@Test
	public void shouldConvertRangeToTheSameRange() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		String result = typeAdapterForDouble.convert("10.0:20.0", true, EConversionMode.QUIET);

		assertEquals("10.0:20.0", result);
	}	

	@Test
	public void shouldConvertRangeToTheFirstNumber() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		String result = typeAdapterForDouble.convert("10.0:20.0", false, EConversionMode.QUIET);

		assertEquals("10.0", result);
	}

	@Test
	public void shouldConvertAlphaToDefaultRange() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		String result = typeAdapterForDouble.convert("ABC", true, EConversionMode.QUIET);

		assertEquals("0.0:0.0", result);
	}

	@Test
	public void shouldConvertAlphaToDefaultNumber() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		String result = typeAdapterForDouble.convert("ABC", false, EConversionMode.QUIET);

		assertEquals("0.0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultRange() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		String result = typeAdapterForDouble.convert("1.0:2.0:E", true, EConversionMode.QUIET);

		assertEquals("0.0:0.0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultNumber() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		String result = typeAdapterForDouble.convert("1.0:2.0:E", false, EConversionMode.QUIET);

		assertEquals("0.0", result);
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowWhenInvalidValue() {

		TypeAdapterForDouble typeAdapterForDouble = new TypeAdapterForDouble();

		typeAdapterForDouble.convert("AB", false, EConversionMode.WITH_EXCEPTION);
	}	
}