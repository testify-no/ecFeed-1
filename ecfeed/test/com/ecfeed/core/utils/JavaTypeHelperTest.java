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
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;

public class JavaTypeHelperTest {

	@Test
	public void testMinusMinMaxForDouble() {

		Double result = JavaTypeHelper.convertNumericToDouble(
				JavaTypeHelper.TYPE_NAME_DOUBLE, "-MIN_VALUE", EConversionMode.QUIET);

		assertEquals((-1)*Double.MIN_VALUE, result, Double.MIN_VALUE);

		result = JavaTypeHelper.convertNumericToDouble(
				JavaTypeHelper.TYPE_NAME_DOUBLE, "-MAX_VALUE", EConversionMode.QUIET);

		assertEquals((-1)*Double.MAX_VALUE, result, Double.MIN_VALUE);
	}

	@Test
	public void testMinusMinMaxForInt() {

		Double result = JavaTypeHelper.convertNumericToDouble(
				JavaTypeHelper.TYPE_NAME_INT, "-MIN_VALUE", EConversionMode.QUIET);

		assertNull(result);

		result = JavaTypeHelper.convertNumericToDouble(
				JavaTypeHelper.TYPE_NAME_INT, "-MAX_VALUE", EConversionMode.QUIET);

		assertNull(result);
	}	

}
