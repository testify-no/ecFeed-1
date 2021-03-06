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

//	protected Set<String> getTestSuites(FrameworkMethod method) throws RunnerException{
//		Set<String> result;
//		Annotation annotation = method.getAnnotation(TestSuites.class);
//		if(annotation != null){
//			result = new HashSet<String>(Arrays.asList(((TestSuites)annotation).value()));
//		}
//		else{
//			result = getMethodModel(getModel(), method).getTestCaseNames();
//		}
//		return result;
//	}
//
//	private Collection<TestCaseNode> getTestCases(MethodNode methodModel, Set<String> testSuites) {
//		Collection<TestCaseNode> result = new LinkedList<TestCaseNode>();
//		for(String testSuite : testSuites){
//			result.addAll(getImplementedTestCases(methodModel, testSuite));
//		}
//		return result;
//	}
//
//	private LinkedList<TestCaseNode> getImplementedTestCases(MethodNode methodModel, String testSuite) {
//		LinkedList<TestCaseNode> result = new LinkedList<TestCaseNode>();
//		for (TestCaseNode testCase : methodModel.getTestCases(testSuite)) {
//			if (implementationStatus(testCase) == EImplementationStatus.IMPLEMENTED) {
//				result.add(testCase);
//			}
//		}
//		return result;
//	}
}