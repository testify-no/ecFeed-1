package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;

public class TypeAdapterForByteTest {

	@Test
	public void shouldConvertNumberToRange() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10", true, EConversionMode.QUIET);

		assertEquals("10:10", result);
	}

	@Test
	public void shouldConvertNumberToTheSameValue() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10", false, EConversionMode.QUIET);

		assertEquals("10", result);
	}	

	@Test
	public void shouldConvertRangeToTheSameRange() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10:12", true, EConversionMode.QUIET);

		assertEquals("10:12", result);
	}	

	@Test
	public void shouldConvertRangeToTheFirstNumber() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10:12", false, EConversionMode.QUIET);

		assertEquals("10", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultRange() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("ABC", true, EConversionMode.QUIET);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertAlphaToDefaultNumber() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("ABC", false, EConversionMode.QUIET);

		assertEquals("0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultRange() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10:11:12", true, EConversionMode.QUIET);

		assertEquals("0:0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultNumber() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		String result = typeAdapterForByte.convert("10:11:12", false, EConversionMode.QUIET);

		assertEquals("0", result);
	}	

	@Test(expected = RuntimeException.class)
	public void shouldThrowWhenInvalidValue() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		typeAdapterForByte.convert("A", false, EConversionMode.WITH_EXCEPTION);
	}
	
	@Test
	public void shouldGenerateValue1() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		Byte result = typeAdapterForByte.generateValue("3:7");		

		checkRange(result, (byte)3, (byte)7);
	}
	
	@Test
	public void shouldGenerateValue2() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		Byte result = typeAdapterForByte.generateValue("MIN_VALUE:MAX_VALUE");		

		checkRange(result, Byte.MIN_VALUE, Byte.MAX_VALUE);
	}

	private void checkRange(Byte result, Byte min, Byte max) {

		if (result >= min && result <= max) {
			return;
		}

		fail();
	}
	

}
