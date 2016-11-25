/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.runner.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.runner.ITestMethodInvoker;

public class ExportTestMethodInvoker implements ITestMethodInvoker {

	MethodNode fMethodNode;
	List<MethodParameterNode> fMethodParameters;
	ArrayList<TestCaseNode> fTestCaseNodes;

	@Override
	public boolean isClassInstanceRequired() {
		return false;	
	}

	public ExportTestMethodInvoker(MethodNode methodNode) {
		fMethodNode = methodNode;
		fTestCaseNodes = new ArrayList<TestCaseNode>();
		fMethodParameters = fMethodNode.getMethodParameters();
	}

	@Override
	public void invoke(
			Method testMethod, 
			String className, 
			Object instance,
			Object[] arguments, 
			Object[] choiceNames,
			String argumentsDescription) throws RuntimeException {

		fTestCaseNodes.add(createTestCase(arguments));
	}

	private TestCaseNode createTestCase(Object[] arguments) {
		List<ChoiceNode> choiceNodes = new ArrayList<ChoiceNode>();

		for (int cnt = 0; cnt < fMethodNode.getParametersCount(); ++cnt) {
			MethodParameterNode methodParameterNode = fMethodParameters.get(cnt);
			choiceNodes.add(new ChoiceNode(methodParameterNode.getName(), arguments[cnt].toString()));
		}

		TestCaseNode testCaseNode = new TestCaseNode("name", choiceNodes); 
		testCaseNode.setParent(fMethodNode);

		return testCaseNode;
	}

	public Collection<TestCaseNode> getTestCasesToExport() {
		return fTestCaseNodes; 
	}

}