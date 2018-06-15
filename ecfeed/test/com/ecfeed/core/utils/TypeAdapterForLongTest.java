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

import static org.junit.Assert.fail;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;

public class TypeAdapterForLongTest {

	@Test(expected = RuntimeException.class)
	public void shouldThrowWhenInvalidValue() {

		TypeAdapterForLong typeAdapterForLong = new TypeAdapterForLong();

		typeAdapterForLong.convert("AB", false, EConversionMode.WITH_EXCEPTION);
	}	

	@Test
	public void shouldGenerateValue1() {

		TypeAdapterForLong typeAdapterForLong = new TypeAdapterForLong();

		Long max = Long.MAX_VALUE;
		Long maxMinusOne = Long.MAX_VALUE - 1;

		String maxStr = max.toString();
		String maxMinusOneStr = maxMinusOne.toString();

		Long result = typeAdapterForLong.generateValue(maxMinusOneStr + ":" + maxStr);		

		checkRange(result, maxMinusOne, max);
	}

	@Test
	public void shouldGenerateValue2() {

		TypeAdapterForLong typeAdapterForLong = new TypeAdapterForLong();

		Long result = typeAdapterForLong.generateValue("0:MAX_VALUE");		

		checkRange(result, 0L, Long.MAX_VALUE);
	}	

	private void checkRange(Long result, Long min, Long max) {

		if (result >= min && result <= max) {
			return;
		}

		fail();
	}

	@Test(expected = RuntimeException.class)
	public void shouldFailToGenerateWhenRangeIsInvalid1() {

		TypeAdapterForLong typeAdapterForLong = new TypeAdapterForLong();

		typeAdapterForLong.generateValue("MAX_VALUE:MIN_VALUE");		
	}	

	@Test(expected = RuntimeException.class)
	public void shouldFailToGenerateWhenRangeIsInvalid2() {

		TypeAdapterForLong typeAdapterForLong = new TypeAdapterForLong();

		typeAdapterForLong.generateValue("1.0:-1.0");		
	}	
}
