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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CulpritTest {
	
	
	@Test
	public void ShouldReturnOccurenceAndFailureCount(){
		List<DimItem> Items = new ArrayList<DimItem>();
		Culprit culprit = new Culprit();
		assertEquals(0, culprit.getFailureCount());
		assertEquals(0, culprit.getOccurenceCount());
		assertEquals(Items, culprit.getItem());		
	}
	
	@Test
	public void ShouldIncrementOccurenceAndFailureCount(){
		Culprit culprit = new Culprit();
		culprit.incrementFailures(culprit.getFailureCount());
		assertEquals(1, culprit.getFailureCount());
		culprit.incrementOccurenceCount(culprit.getOccurenceCount());
		assertEquals(1, culprit.getOccurenceCount());
		culprit.aggregateOccurencesAndFailures(culprit);
		assertEquals(2, culprit.getFailureCount());
	}
	
	@Test
	public void ShouldSayIfTheTupleMatchesAnyOther(){
		Culprit candidate = new Culprit();
		Culprit culprit = new Culprit();
		assertTrue(culprit.isTupleMatch(candidate));
		
		
	}
	


}
