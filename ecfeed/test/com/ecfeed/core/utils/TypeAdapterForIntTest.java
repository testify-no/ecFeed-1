package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;

public class TypeAdapterForIntTest {

	@Test
	public void shouldConvertNumberToRange() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		String result = typeAdapterForInt.convert("10", true, EConversionMode.QUIET);

		assertEquals("10:10", result);
	}

	@Test
	public void shouldConvertNumberToTheSameValue() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		String result = typeAdapterForInt.convert("10", false, EConversionMode.QUIET);

		assertEquals("10", result);
	}

	@Test
	public void shouldConvertRangeToTheSameRange() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		String result = typeAdapterForInt.convert("10:11", true, EConversionMode.QUIET);

		assertEquals("10:11", result);
	}	

	@Test
	public void shouldConvertRangeToTheFirstNumber() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		String result = typeAdapterForInt.convert("10:11", false, EConversionMode.QUIET);

		assertEquals("10", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultRange() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		String result = typeAdapterForInt.convert("ABC", true, EConversionMode.QUIET);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultNumber() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		String result = typeAdapterForInt.convert("ABC", false, EConversionMode.QUIET);

		assertEquals("0", result);
	}	
	
	@Test
	public void shouldConvertInvalidRangeToDefaultRange() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		String result = typeAdapterForInt.convert("10:11:12", true, EConversionMode.QUIET);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultNumber() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		String result = typeAdapterForInt.convert("10:11:12", false, EConversionMode.QUIET);

		assertEquals("0", result);
	}
	
	@Test(expected = RuntimeException.class)
	public void shouldThrowWhenInvalidValue() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		typeAdapterForInt.convert("AB", false, EConversionMode.WITH_EXCEPTION);
	}	

}
