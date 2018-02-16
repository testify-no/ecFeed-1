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

import java.lang.reflect.Method;
import java.util.Collection;

import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.EcException;

public class StaticRunnerMethod extends AbstractFrameworkMethod {

	private Collection<TestCaseNode> fTestCases;

	public StaticRunnerMethod(Method method, Collection<TestCaseNode> testCases, ModelClassLoader loader) {
		super(method, loader);
		fTestCases = testCases;
	}

	@Override
	public Object invokeExplosively(Object target, Object... parameters) throws Throwable{

		int totalTestCaseCounter = 0;
		int failedTestCaseCounter = 0;
		StringBuilder stringBuilder = new StringBuilder();

		for (TestCaseNode testCase : fTestCases) {

			totalTestCaseCounter++;

			try {
				super.invoke(target, testCase.getTestData());

			} catch (Throwable e) {

				if (failedTestCaseCounter == 0) {
					JavaMethodHelper.appendFailedTestsMessage(stringBuilder);
				}

				failedTestCaseCounter++;
				JavaMethodHelper.appendExceptionMessage(
						testCase.getMethod().getName(),
						testCase.getTestData(),
						e, 
						stringBuilder);
			}
		}

		JavaMethodHelper.addTestStatistics(totalTestCaseCounter, failedTestCaseCounter, stringBuilder);

		if (failedTestCaseCounter > 0) {
			EcException.report(stringBuilder.toString());
		}

		return null;
	}

}
