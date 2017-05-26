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

		String item = new String();
		int dim = 0;

		DimItem dimitem = new DimItem(dim, item);

		assertEquals(item, dimitem.getItem());
		assertEquals(dim, dimitem.getDim());
	}

	@Test
	public void shouldCompareItems(){

		DimItem dimitem1 = new DimItem(0, new String());

		DimItem dimitem2 = new DimItem(0, new String());
		assertFalse(dimitem1.isMatch(dimitem2));

		DimItem candidate3 = new DimItem(10, "blabla");
		assertFalse(dimitem1.isMatch(candidate3));
	}
}
