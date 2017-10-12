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
import com.ecfeed.ui.common.Messages;

public class ExecutionTestInformer extends AbstractTestInformer {
	

	public ExecutionTestInformer(MethodNode methodNode, TestResultsHolder testResultsHolder)
	{
		super(methodNode, testResultsHolder, Messages.EXECUTING_TEST_WITH_PARAMETERS);
	}
	
	@Override
	protected void setTestProgressMessage() {
		String message = "Executed: " + getExecutedTestCases() + "  Failed: " + getFailedTestCases();		
		fProgressMonitor.subTask(message);
	}

}
