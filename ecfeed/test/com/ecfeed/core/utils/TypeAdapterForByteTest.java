package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TypeAdapterForByteTest {

	@Test
	public void shouldConvertNumberToRange() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10", true);

		assertEquals("10:10", result);
	}

	@Test
	public void shouldConvertNumberToTheSameValue() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10", false);

		assertEquals("10", result);
	}	

	@Test
	public void shouldConvertRangeToTheSameRange() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10:12", true);

		assertEquals("10:12", result);
	}	

	@Test
	public void shouldConvertRangeToTheFirstNumber() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10:12", false);

		assertEquals("10", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultRange() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("ABC", true);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultNumber() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("ABC", false);

		assertEquals("0", result);
	}	
	
	@Test
	public void shouldConvertInvalidRangeToDefaultRange() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10:11:12", true);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultNumber() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10:11:12", false);

		assertEquals("0", result);
	}	
	
}
