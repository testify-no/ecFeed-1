/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import java.util.Collection;

import com.ecfeed.core.adapter.java.AdapterConstants;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;

public class MethodOperationRenameTestCases extends BulkOperation {

	public MethodOperationRenameTestCases(
			Collection<TestCaseNode> testCases, 
			String newName) throws ModelOperationException {

		super(OperationNames.RENAME_TEST_CASE, false, getFirstParent(testCases), getFirstParent(testCases));

		if (newName.matches(AdapterConstants.REGEX_TEST_CASE_NODE_NAME) == false) {
			ModelOperationException.report(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}

		for(TestCaseNode testCase : testCases){
			addOperation(FactoryRenameOperation.getRenameOperation(testCase, newName));
		}
	}

	private static AbstractNode getFirstParent(Collection<TestCaseNode> testCases) {

		if (testCases.isEmpty()) {
			return null;
		}

		for (TestCaseNode testCaseNode : testCases) {
			return testCaseNode.getParent();
		}

		return null;
	}
}
