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

public class CulpritFinder {
	
	
	FinderAnalysis fFinderAnalysis = new FinderAnalysis();

	FinderAnalysis generateAnalysis(List<TestResult> testresults){
		for (TestResult testresult : testresults){
			processTestResult(testresult);
			
		}
		return fFinderAnalysis;
	}
	
	private void processTestResult(TestResult testresult){
		
	}
	
//	private void processTestResult(TestResult testresult){
//		for(.......) { // this is probably nested for or something similar - 
//			//here we have to generate Culprits i.e. to find all pairs, 3-tuples, 4-tuples etc and pass it to finder analisys 
//			Culprit culprit = new Culprit(........) // having n-tuple we have to generate a Culprit (add nulls)
//			fFinderAnalisys.aggregateCulprit(culprit);
//
//		}
//
//	}
}