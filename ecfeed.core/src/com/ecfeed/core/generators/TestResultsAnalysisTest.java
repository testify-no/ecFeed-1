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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestResultsAnalysisTest {
	
	@Test
	public void shouldReturnFailureCount() {
		List<DimItem> item = new ArrayList<DimItem>();
		Culprit culprit = new Culprit();
		
		TestResultsAnalysis finderanalysis = new TestResultsAnalysis();
		finderanalysis.aggregateCulprit(culprit);
		
		assertEquals(culprit.getFailureCount(), 0);
	}
	
	@Test
	public void shouldReturnOccuranceCout() {
		List<DimItem> item = new ArrayList<DimItem>();
		//Culprit culprit = new Culprit
	}
}
