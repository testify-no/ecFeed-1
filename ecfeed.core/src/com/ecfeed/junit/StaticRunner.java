/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.junit;

import java.util.Collection;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.runner.RunnerException;

public class StaticRunner extends AbstractStaticRunner {

	public StaticRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected void addMethodsForOneCustomMethod(
			FrameworkMethod frameworkMethod,
			MethodNode methodNode,
			List<FrameworkMethod> outFrameworkMethods) throws RunnerException {

		Collection<TestCaseNode> testCases = getTestCases(methodNode, getTestSuites(frameworkMethod));

		for (TestCaseNode testCaseNode : testCases) {

			addMethodWithOneTestCase( 
					frameworkMethod,
					testCaseNode,
					outFrameworkMethods);
		}
	}

	private void addMethodWithOneTestCase( 
			FrameworkMethod frameworkMethod,
			TestCaseNode testCaseNode,
			List<FrameworkMethod> outFrameworkMethods) {

		outFrameworkMethods.add(
				new SimpleRunnerMethod(frameworkMethod.getMethod(), testCaseNode.getTestData(), getLoader()));
	}	

}
