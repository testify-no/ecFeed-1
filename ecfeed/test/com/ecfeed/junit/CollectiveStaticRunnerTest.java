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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.core.serialization.IModelParser;
import com.ecfeed.core.serialization.ParserException;
import com.ecfeed.core.serialization.ect.EctParser;
import com.ecfeed.junit.CollectiveStaticRunner;
import com.ecfeed.junit.annotations.EcModel;
import com.ecfeed.junit.annotations.TestSuites;

public class CollectiveStaticRunnerTest extends CollectiveStaticRunner{
	public CollectiveStaticRunnerTest() throws InitializationError {
		super(CollectiveStaticRunnerTest.class);
	}

	protected final static String MODEL_PATH = "test/com/ecfeed/junit/CollectiveStaticRunnerTest.ect";
	protected final static String TEST_SUITES[] = {"Test Suite 1", "Test Suite 2"};

	protected static Set<List<Integer>> fExecutedTestCases;

	@RunWith(CollectiveStaticRunner.class)
	@EcModel(MODEL_PATH)
	public static class TestClass{
		@Test
		public void noArgsTestFunction(){
			if(fExecutedTestCases != null){
				fExecutedTestCases.add(new ArrayList<Integer>());
			}
		}

		@Test
		public void noTestSuitesTestFunction(int arg1, int arg2){
			List<Integer> args = new ArrayList<Integer>();
			args.add(arg1); 
			args.add(arg2);
			if(fExecutedTestCases != null){
				fExecutedTestCases.add(args);
			}
		}

		@Test
		@TestSuites({"Test Suite 1", "Test Suite 2"})
		public void testSuitesFunction(int arg1, int arg2){
			List<Integer> args = new ArrayList<Integer>();
			args.add(arg1); 
			args.add(arg2);
			if(fExecutedTestCases != null){
				fExecutedTestCases.add(args);
			}
		}
	}

	@Test
	public void frameworkMethodsTest(){
		try {
			CollectiveStaticRunner runner = new CollectiveStaticRunner(TestClass.class);
			List<FrameworkMethod> methods = runner.computeTestMethods();
			RootNode model = getModel(MODEL_PATH);
			TestClass target = new TestClass();
			for(FrameworkMethod method : methods){
				try {
					fExecutedTestCases = new HashSet<List<Integer>>();
					try {
						method.invokeExplosively(target, (Object[])null);
					} catch (Throwable e) {
						fail("Unexpected invokation exception: " + e.getMessage());
					}
					MethodNode methodModel = getMethodModel(model, method);
					Set<List<Integer>> referenceResult = referenceResult(method, methodModel); 
					assertEquals(referenceResult, fExecutedTestCases);
				} catch (RunnerException e) {
					fail("Unexpected runner exception: " + e.getMessage());
				}
			}
		} catch (InitializationError e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	private Set<List<Integer>> referenceResult(FrameworkMethod method,
			MethodNode methodModel) {
		Set<List<Integer>> result = new HashSet<List<Integer>>();
		if(method.getMethod().getParameterTypes().length == 0){
			result.add(new ArrayList<Integer>());
		}
		else{
			Set<String> testSuites;
			testSuites = getTestSuites(method, methodModel);
			Collection<TestCaseNode> testCases = getTestCases(methodModel, testSuites);
			for(TestCaseNode testCase : testCases){
				addTestCaseResult(testCase, result);
			}
		}
		return result;
	}

	private Set<String> getTestSuites(FrameworkMethod method, MethodNode methodModel){
		Set<String> result;
		Annotation annotation = method.getAnnotation(TestSuites.class);
		if(annotation != null){
			result = new HashSet<String>(Arrays.asList(((TestSuites)annotation).value()));
		}
		else{
			result = methodModel.getTestCaseNames();
		}
		return result;
	}

	private void addTestCaseResult(TestCaseNode testCase,
			Set<List<Integer>> target) {
		List<Integer> result = new ArrayList<Integer>();
		for(ChoiceNode parameter : testCase.getTestData()){
			result.add(Integer.valueOf(parameter.getValueString()));
		}
		target.add(result);
	}

	protected Collection<TestCaseNode> getTestCases(MethodNode methodModel,
			Set<String> testSuites) {
		Collection<TestCaseNode> testCases = new HashSet<TestCaseNode>();
		if(testSuites.size() == 0){
			testCases = methodModel.getTestCases();
		}
		else{
			for(String testSuite : testSuites){
				testCases.addAll(methodModel.getTestCases(testSuite));
			}
		}
		return testCases;
	}

	protected RootNode getModel(String path){
		IModelParser parser = new EctParser();
		InputStream istream;
		try {
			istream = new FileInputStream(new File(path));
			return parser.parseModel(istream);
		} catch (FileNotFoundException e) {
			fail("Cannot find file: " + path);
			return null;
		} catch (ParserException e) {
			fail("Cannot parse file " + path + ": " + e.getMessage());
			return null;
		}
	}
}
