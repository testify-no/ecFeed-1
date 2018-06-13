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

	@Test(expected = RuntimeException.class)
	public void shouldNotConvertRangeWhenNotRandomized() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		typeAdapterForByte.convert("0:0", false, EConversionMode.WITH_EXCEPTION);
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

	@Test(expected = RuntimeException.class)
	public void shouldFailToGenerateWhenRangeIsInvalid1() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		typeAdapterForByte.generateValue("MAX_VALUE:MIN_VALUE");		
	}	

	@Test(expected = RuntimeException.class)
	public void shouldFailToGenerateWhenRangeIsInvalid2() {

		TypeAdapterForByte typeAdapterForByte = new TypeAdapterForByte();

		typeAdapterForByte.generateValue("1:-1");		
	}	

}
