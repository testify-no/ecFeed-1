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

public class FinderAnalysis {
	
	List<String> testcases = new ArrayList<String>();
	boolean result = false;
	
	TestResult testresult = new TestResult(testcases, result);
	
	List<Culprit> culprits;
	
	private void aggregateCulprit(Culprit culprit){
		for (int i = 0; i < culprits.size(); i++){
			if(culprits.get(i) != culprit){
				culprit.fFailureCount += 1;
				if(!testresult.getResult()){
					culprit.fFailureCount += 1;
				}
			}
			else{
				culprits.add(culprit);	
			}
		}
		
	}

}
