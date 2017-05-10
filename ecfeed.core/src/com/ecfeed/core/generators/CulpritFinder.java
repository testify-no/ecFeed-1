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

import java.util.List;

import com.ecfeed.core.generators.algorithms.Tuples;

public class CulpritFinder {
	
	
	FinderAnalysis fFinderAnalysis = new FinderAnalysis();

	FinderAnalysis generateAnalysis(List<TestResult> testresults){
		for (TestResult testresult : testresults){
			processTestResult(testresult);
			
		}
		return fFinderAnalysis;
	}
	
	private void processTestResult(TestResult testresult){

		for(int index = 0; index < testresult.getTestCases().size(); index++){
			Tuples<String> tuples = new Tuples<String>(testresult.getTestCases(), index);
			Culprit culprit = new Culprit(tuples, testresult.getResult());
			fFinderAnalysis.aggregateCulprit(culprit);
		}
	}
}