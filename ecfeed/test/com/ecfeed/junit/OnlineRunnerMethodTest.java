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

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.generators.CartesianProductGenerator;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.core.runner.java.JUnitTestMethodInvoker;
import com.ecfeed.junit.AbstractFrameworkMethod;
import com.ecfeed.junit.AndroidRuntimeMethod;
import com.ecfeed.junit.CollectiveOnlineRunnerMethod;

public class OnlineRunnerMethodTest {

	private Set<List<Integer>> fExecuted;
	private final int MAX_PARTITIONS = 10;

	private final Collection<IConstraint<ChoiceNode>> EMPTY_CONSTRAINTS = 
			new ArrayList<IConstraint<ChoiceNode>>();

	public void functionUnderTest(int arg1, int arg2){
		List<Integer> parameters = new ArrayList<Integer>();
		parameters.add(arg1);
		parameters.add(arg2);
		fExecuted.add(parameters);
	}

	@Test
	public void javaMethodTest(){
		for(int j = 1; j <= MAX_PARTITIONS; ++j){
			test(false, 2, j);
		}
	}

	@Test
	public void androidMethodTest(){
		for(int j = 1; j <= MAX_PARTITIONS; ++j){
			test(true, 2, j);
		}
	}	

	public void test(boolean isAndroidTest, int parameters, int choicesPerParameter) {
		List<List<ChoiceNode>> input = generateInput(parameters, choicesPerParameter);
		IGenerator<ChoiceNode> generator = new CartesianProductGenerator<ChoiceNode>();
		try {
			Method methodUnterTest = this.getClass().getMethod("functionUnderTest", int.class, int.class);
			generator.initialize(input, EMPTY_CONSTRAINTS, null, null);

			String className = this.getClass().getName();
			AbstractFrameworkMethod testedMethod =
					createFrameworkMethod(isAndroidTest, className, methodUnterTest, generator);

			fExecuted = new HashSet<List<Integer>>();
			testedMethod.invokeExplosively(this, (Object[])null);
			assertEquals(referenceResult(input), fExecuted);
		} catch (Throwable e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	private AbstractFrameworkMethod createFrameworkMethod(
			boolean isAndroidTest, 
			String className,
			Method methodUnterTest, 
			IGenerator<ChoiceNode> generator) throws RunnerException {

		if (isAndroidTest) { 
			return new CollectiveOnlineRunnerMethod(
					methodUnterTest, 
					generator, 
					new ModelClassLoader(new URL[]{}, this.getClass().getClassLoader()));
		} else {
			return new AndroidRuntimeMethod(
					className,
					methodUnterTest, 
					generator, 
					new ModelClassLoader(new URL[]{}, this.getClass().getClassLoader()), 
					new JUnitTestMethodInvoker());
		}
	}

	private Set<List<Integer>> referenceResult(List<List<ChoiceNode>> input) {
		Set<List<Integer>> result = new HashSet<List<Integer>>();
		CartesianProductGenerator<ChoiceNode> referenceGenerator = new CartesianProductGenerator<ChoiceNode>();
		try {
			referenceGenerator.initialize(input, EMPTY_CONSTRAINTS, null, null);
			List<ChoiceNode> next;
			while((next = referenceGenerator.next()) != null){
				List<Integer> testCase = new ArrayList<Integer>();
				for(ChoiceNode parameter : next){
					testCase.add(Integer.valueOf(parameter.getValueString()));
				}
				result.add(testCase);
			}
		} catch (GeneratorException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
		return result;
	}

	private List<List<ChoiceNode>> generateInput(int parameters,
			int choices) {
		List<List<ChoiceNode>> input = new ArrayList<List<ChoiceNode>>();
		for(int i = 0; i < parameters; ++i){
			input.add(generateParameter(choices));
		}
		return input;
	}

	private List<ChoiceNode> generateParameter(int choices) {
		MethodParameterNode parent = new MethodParameterNode("Parameter", "int","0",  false);
		List<ChoiceNode> parameter = new ArrayList<ChoiceNode>();
		for(int i = 0; i < choices; i++){
			ChoiceNode choice = new ChoiceNode(String.valueOf(i), String.valueOf(i));
			choice.setParent(parent);
			parameter.add(choice);
		}
		return parameter;
	}

}
