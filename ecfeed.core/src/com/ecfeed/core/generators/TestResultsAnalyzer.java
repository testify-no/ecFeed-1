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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.generators.algorithms.Tuples;

public class TestResultsAnalyzer {

	TestResultsAnalysis fTestResultAnalysis = new TestResultsAnalysis();

	public TestResultsAnalysis generateAnalysis(List<TestResultDescription> testresults, int n1, int n2) {
		
		for (TestResultDescription testresult : testresults) {
			processTestResult(testresult, n1, n2);
		}
		
		return fTestResultAnalysis;
	}
	
	private void processTestResult(TestResultDescription testresult, int n1, int n2) {
		
		for (int n = n1; n <n2; n++) {
			processTestResultForN(testresult, n);
		}
	}

	private void processTestResultForN(TestResultDescription testresult, int n) {
		
		Tuples<DimItem> tuples = new Tuples<DimItem>(
				createListOfDimItems(testresult.getTestArguments()), n);
		
		Set<List<DimItem>> allTuples = tuples.getAll();
		
		for (List<DimItem> tuple: allTuples) {
			aggregateTuple(tuple, testresult.getResult());
		}
	}
	
	private void aggregateTuple(List<DimItem> tuple, boolean result) {
		
		Culprit culprit = new Culprit(tuple);
		
		if (!result) {
			culprit.aggregateOccurencesAndFailures(culprit);	
		} else {
			culprit.incrementOccurenceCount(culprit.getOccurenceCount());
		}
		
		fTestResultAnalysis.aggregateCulprit(culprit);
	}

	private List<DimItem> createListOfDimItems(List<String> items) {
		
		List<DimItem> dimItems = new ArrayList<DimItem>();
		int dim = 0;
		
		for (String item: items) {
			DimItem dimitem = new DimItem(item, dim);
			dimItems.add(dimitem);
			dim += 1;
		}
		
		return dimItems;
	}
	
}