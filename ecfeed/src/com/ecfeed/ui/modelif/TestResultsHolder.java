/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.generators.TestResultDescription;
import com.ecfeed.core.model.ChoiceNode;


public class TestResultsHolder {
	private List<TestResultDescription> fTestResultDescrs;

	public TestResultsHolder(){
		fTestResultDescrs = new ArrayList<TestResultDescription>();
	}

	public void addTestResult(List<ChoiceNode> testInput, boolean result) {
		
		List<String> input = new ArrayList<String>();

		for (ChoiceNode node: testInput){
			input.add(node.getValueString());
		}
		
		fTestResultDescrs.add(new TestResultDescription(input, result));
	}

	public TestResultDescription getTestResultDescription(int index) {
		
		return fTestResultDescrs.get(index);
	}	

	public List<TestResultDescription> getTestResultDescription() {
		
		return fTestResultDescrs;
	}
}

