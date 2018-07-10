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

	@Test(expected = RuntimeException.class)
	public void shouldThrowWhenInvalidValue() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		typeAdapterForShort.convert("AB", false, EConversionMode.WITH_EXCEPTION);
	}

	@Test
	public void shouldGenerateValue1() {

		TypeAdapterForShort typeAdapterForShort= new TypeAdapterForShort();

		Short result = typeAdapterForShort.generateValue("1:2");		

		checkRange(result, (short)1, (short)2);
	}

	@Test
	public void shouldGenerateValue2() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		Short result = typeAdapterForShort.generateValue("MIN_VALUE:MAX_VALUE");		

		checkRange(result, Short.MIN_VALUE, Short.MAX_VALUE);
	}
	
	@Test
	public void shouldGenerateValue3() {

		TypeAdapterForShort typeAdapterForShort = new TypeAdapterForShort();

		Short result = typeAdapterForShort.generateValue("-4:-4");		

		checkRange(result, (short)-4, (short)-4);
	}	

	private void checkRange(Short result, Short min, Short max) {

		if (result >= min && result <= max) {
			return;
		}

		fail();
	}

}
