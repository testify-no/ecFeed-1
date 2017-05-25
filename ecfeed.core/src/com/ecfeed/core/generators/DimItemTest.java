/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class DimItemTest {

	@Test
	public void shouldReturnDimensionItem() {

		int item = 0;
		String dim = new String();

		DimItem dimitem = new DimItem(dim, item);

		assertEquals(item, dimitem.getItem());
		assertEquals(dim, dimitem.getDim());
	}

	@Test
	public void shouldCompareTuples(){
		int item = 0;
		String dim = new String();
		DimItem dimitem = new DimItem(dim, item);

		int item2 = 10;
		String dim2 = "blabla";
		DimItem candidate = new DimItem(dim2, item2);
		assertFalse(dimitem.isMatch(candidate));

	}
}
