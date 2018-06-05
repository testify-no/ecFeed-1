package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;

public class TypeAdapterForBooleanTest {

	@Test
	public void shouldConvertNumberToFalse() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		String result = typeAdapterForBoolean.convert("10", false, EConversionMode.QUIET);

		assertEquals("false", result);
	}

	@Test
	public void shouldConvertFalseToFalse() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		String result = typeAdapterForBoolean.convert("false", false, EConversionMode.QUIET);

		assertEquals("false", result);
	}

	@Test
	public void shouldConvertFalseToFalse2() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		String result = typeAdapterForBoolean.convert("FaLsE", false, EConversionMode.QUIET);

		assertEquals("false", result);
	}	

	@Test
	public void shouldConvertTrueToTrue() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		String result = typeAdapterForBoolean.convert("true", false, EConversionMode.QUIET);

		assertEquals("true", result);
	}

	@Test
	public void shouldConvertTrueToTrue2() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		String result = typeAdapterForBoolean.convert("TrUe", false, EConversionMode.QUIET);

		assertEquals("true", result);
	}	

	@Test
	public void shouldNotThrowWhenRandomized() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		typeAdapterForBoolean.convert("true", true, EConversionMode.QUIET);
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowWhenInvalidValue() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		typeAdapterForBoolean.convert("TrUe", false, EConversionMode.WITH_EXCEPTION);
	}	

}
