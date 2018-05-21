package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TypeAdapterForBooleanTest {

	@Test
	public void shouldConvertNumberToFalse() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		String result = typeAdapterForBoolean.convert("10", false);

		assertEquals("false", result);
	}

	@Test
	public void shouldConvertFalseToFalse() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		String result = typeAdapterForBoolean.convert("false", false);

		assertEquals("false", result);
	}

	@Test
	public void shouldConvertFalseToFalse2() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		String result = typeAdapterForBoolean.convert("FaLsE", false);

		assertEquals("false", result);
	}	

	@Test
	public void shouldConvertTrueToTrue() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		String result = typeAdapterForBoolean.convert("true", false);

		assertEquals("true", result);
	}

	@Test
	public void shouldConvertTrueToTrue2() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		String result = typeAdapterForBoolean.convert("TrUe", false);

		assertEquals("true", result);
	}	

	@Test
	public void shouldNotThrowWhenRandomized() {

		TypeAdapterForBoolean typeAdapterForBoolean = new TypeAdapterForBoolean();

		typeAdapterForBoolean.convert("true", true);
	}	

}
