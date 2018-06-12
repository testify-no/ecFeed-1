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

	@Test
	public void shouldConvertQuietlySpecialValuesMinusMinMax() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		String result = typeAdapterForInt.convert("-MAX_VALUE:-MIN_VALUE", true, EConversionMode.QUIET);

		assertEquals("0:0", result);
	}	

	@Test(expected = RuntimeException.class)
	public void shouldNotConvertSpecialValuesMinusMinMax() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		typeAdapterForInt.convert("-MAX_VALUE:-MIN_VALUE", true, EConversionMode.WITH_EXCEPTION);
	}	

	@Test
	public void shouldGenerateValue1() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		Integer result = typeAdapterForInt.generateValue("0:MAX_VALUE");

		checkRange(result, 0, Integer.MAX_VALUE);
	}	

	@Test(expected = RuntimeException.class)
	public void shouldThrowForInvalidRange() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		typeAdapterForInt.generateValue("MIN_VALUE, MAX_VALUE");
	}

	@Test
	public void shouldGenerateValue2() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		Integer result = typeAdapterForInt.generateValue("MIN_VALUE:MAX_VALUE");

		checkRange(result, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Test
	public void shouldGenerateValue3() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		Integer result = typeAdapterForInt.generateValue("-1:1");

		checkRange(result, -1, 1);
	}	

	@Test
	public void shouldGenerateValue4() {

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		Integer result = typeAdapterForInt.generateValue("1:100");

		checkRange(result, 0, 100);
	}

	private void checkRange(Integer result, Integer min, Integer max) {

		if (result >= min && result <= max) {
			return;
		}

		fail();
	}
}
