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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.junit.annotations.TestSuites;

public abstract class AbstractStaticRunner extends AbstractJUnitRunner {

	public AbstractStaticRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	protected Set<String> getTestSuites(FrameworkMethod frameworkMethod) throws RunnerException {

		Annotation annotation = frameworkMethod.getAnnotation(TestSuites.class);

		if (annotation != null) {
			return new HashSet<String>(Arrays.asList(((TestSuites)annotation).value()));
		}

		return getMethodModel(getModel(), frameworkMethod).getTestSuites();
	}

	protected Collection<TestCaseNode> getTestCases(MethodNode methodNode, Set<String> testSuites) {

		Collection<TestCaseNode> result = new LinkedList<TestCaseNode>();

		for (String testSuite : testSuites) {
			result.addAll(getImplementedTestCases(methodNode, testSuite));
		}

		return result;
	}

	private LinkedList<TestCaseNode> getImplementedTestCases(MethodNode methodNode, String testSuite) {

		LinkedList<TestCaseNode> result = new LinkedList<TestCaseNode>();

		for (TestCaseNode testCase : methodNode.getTestCases(testSuite)) {

			if (getImplementationStatus(testCase) == EImplementationStatus.IMPLEMENTED) {
				result.add(testCase);
			}
		}

		return result;
	}
}
