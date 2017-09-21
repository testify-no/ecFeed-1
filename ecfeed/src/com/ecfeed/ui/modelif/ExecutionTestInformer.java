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

import com.ecfeed.core.model.MethodNode;

public class ExecutionTestInformer extends AbstractTestInformer {
	
	MethodNode fmethodNode;
	TestResultsHolder ftestResultsHolder;

	public ExecutionTestInformer(MethodNode methodNode, TestResultsHolder testResultsHolder)
	{
		fmethodNode = methodNode;
		ftestResultsHolder = testResultsHolder;	
	}
	@Override
	protected void setTestProgressMessage() {
		String message = "Executed: " + getExecutedTestCases() + "  Failed: " + getFailedTestCases();		
		fProgressMonitor.subTask(message);
	}

}
