/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;

public class TypeAdapterForFloatTest {

	@Test
	public void shouldConvertNumberToRange() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("10", true, EConversionMode.QUIET);
		assertEquals("10.0:10.0", result);

		result = typeAdapterForFloat.convert("10.0", true, EConversionMode.QUIET);
		assertEquals("10.0:10.0", result);
	}

	@Test
	public void shouldConvertNumberToTheSameValue() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("10.0", false, EConversionMode.QUIET);

		assertEquals("10.0", result);
	}

	@Test
	public void shouldConvertRangeToTheSameRange() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("10.0:20.0", true, EConversionMode.QUIET);

		assertEquals("10.0:20.0", result);
	}	

	@Test
	public void shouldConvertRangeToTheFirstNumber() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("10.0:20.0", false, EConversionMode.QUIET);

		assertEquals("10.0", result);
	}

	@Test
	public void shouldConvertAlphaToDefaultRange() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("ABC", true, EConversionMode.QUIET);

		assertEquals("0.0:0.0", result);
	}

	@Test
	public void shouldConvertAlphaToDefaultNumber() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("ABC", false, EConversionMode.QUIET);

		assertEquals("0.0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultRange() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("1.0:2.0:E", true, EConversionMode.QUIET);

		assertEquals("0.0:0.0", result);
	}	

	@Test
	public void shouldConvertInvalidRangeToDefaultNumber() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		String result = typeAdapterForFloat.convert("1.0:2.0:E", false, EConversionMode.QUIET);

		assertEquals("0.0", result);
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowWhenInvalidValue() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		typeAdapterForFloat.convert("AB", false, EConversionMode.WITH_EXCEPTION);
	}

	@Test
	public void shouldGenerateValue1() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		Float result = typeAdapterForFloat.generateValue("1.6:1.7");		

		checkRange(result, (float)1.6, (float)1.7);
	}

	@Test
	public void shouldGenerateValue2() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		Float result = typeAdapterForFloat.generateValue("MIN_VALUE:MAX_VALUE");		

		checkRange(result, Float.MIN_VALUE, Float.MAX_VALUE);
	}

	private void checkRange(Float result, Float min, Float max) {

		if (result >= min && result <= max) {
			return;
		}

		fail();
	}

	@Test(expected = RuntimeException.class)
	public void shouldFailToGenerateWhenRangeIsInvalid1() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		typeAdapterForFloat.generateValue("MAX_VALUE:MIN_VALUE");		
	}	

	@Test(expected = RuntimeException.class)
	public void shouldFailToGenerateWhenRangeIsInvalid2() {

		TypeAdapterForFloat typeAdapterForFloat = new TypeAdapterForFloat();

		typeAdapterForFloat.generateValue("1.0:-1.0");		
	}
}
