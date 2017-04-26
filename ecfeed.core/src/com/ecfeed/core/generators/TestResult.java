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

public class TestResult {

	private List<String> ftestcases;
	private boolean fresult;

	public TestResult(List<String> testcases, boolean result) {
		ftestcases = testcases;
		fresult = result;
	}
	List<String> getTestCases(){
		return ftestcases;
	}
	boolean getResult(){
		return fresult;
	}

}
