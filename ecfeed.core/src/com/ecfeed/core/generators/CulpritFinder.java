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

public class CulpritFinder {


	FinderAnalysis fFinderAnalysis = new FinderAnalysis();

	FinderAnalysis generateAnalysis(List<TestResult> testresults, int n1, int n2){
		for (TestResult testresult : testresults){
			processTestResult(testresult, n1, n2);
		}
		return fFinderAnalysis;
	}
	
	private void processTestResult(TestResult testresult, int n1, int n2){
		for(int index = n1; index <n2; index++){
			Tuples<DimItem> tuples = new Tuples<DimItem>(
					createListOfDimItems(testresult.getTestCases()), index);
			Set<List<DimItem>> AllTuples = tuples.getAll();
			for(List<DimItem> tuple: AllTuples){
				aggregateTuple(tuple, testresult.getResult());
			}
		}
	}
	
	private void aggregateTuple(List<DimItem> tuple, boolean result) {
		Culprit culprit = new Culprit(tuple);
		if(!result){
			culprit.aggregateOccurencesAndFailures(culprit);	
		}else{
			culprit.incrementOccurenceCount(culprit.getOccurenceCount());
		}
		fFinderAnalysis.aggregateCulprit(culprit);
	}

	private List<DimItem> createListOfDimItems(List<String> items) {
		List<DimItem> DimItems = new ArrayList<DimItem>();
		int dim = 0;
		for(String item: items){
			DimItem dimitems = new DimItem(item, dim);
			DimItems.add(dimitems);
			dim += 1;
		}
		return DimItems;
	}
}