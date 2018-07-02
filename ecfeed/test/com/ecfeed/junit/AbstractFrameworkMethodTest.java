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
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.adapter.java.ChoiceValueParser;
import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.CommonConstants;
import com.ecfeed.junit.AbstractFrameworkMethod;

public class AbstractFrameworkMethodTest {

	private final String FUNCTION_UNDER_TEST_NAME = "functionUnderTest";

	private final List<String> INT_ARGS;
	private final List<String> STRING_ARGS;
	private final List<String> ENUM_ARGS;

	private Result fResult = new Result();

	public enum Enum{
		VALUE1, VALUE2, VALUE3, VALUE4
	}

	public static class Result{
		int arg1;
		String arg2;
		Enum arg3;

		public void reset(){
			arg1 = 0;
			arg2 = null;
			arg3 = null;
		}
	}

	public AbstractFrameworkMethodTest(){
		INT_ARGS = new ArrayList<String>();
		INT_ARGS.addAll(Arrays.asList(CommonConstants.INTEGER_SPECIAL_VALUES));
		INT_ARGS.addAll(Arrays.asList(new String[]{"-1", "0", "1"}));
		STRING_ARGS = new ArrayList<String>();
		STRING_ARGS.addAll(Arrays.asList(com.ecfeed.core.utils.CommonConstants.STRING_SPECIAL_VALUES));
		STRING_ARGS.addAll(Arrays.asList(new String[]{"", "a", "Aa"}));
		ENUM_ARGS = new ArrayList<String>();
		for(Enum e : Enum.values()){
			ENUM_ARGS.add(e.name());
		}
	}

	public void functionUnderTest(int arg1, String arg2, Enum arg3){
		fResult.reset();

		fResult.arg1 = arg1;
		fResult.arg2 = arg2;
		fResult.arg3 = arg3;
	}

	@Test
	public void invokeTest() {
		try {
			Method method = this.getClass().getMethod(FUNCTION_UNDER_TEST_NAME, int.class, String.class, Enum.class);
			MethodParameterNode intParameter = new MethodParameterNode("intParameter", "int", "0", false);
			MethodParameterNode stringParameter = new MethodParameterNode("stringParameter", "String", "0", false);
			MethodParameterNode enumParameter = new MethodParameterNode("enumParameter", Enum.class.getCanonicalName(), Enum.values()[0].name(), false);
			ChoiceValueParser parser = new ChoiceValueParser(new ModelClassLoader(new URL[]{}, this.getClass().getClassLoader()), false);

			ModelClassLoader loader = new ModelClassLoader(new URL[]{}, this.getClass().getClassLoader());
			AbstractFrameworkMethod frameworkMethod = new AbstractFrameworkMethod(method, loader);
			for(String intArg : INT_ARGS){
				for(String stringArg : STRING_ARGS){
					for(String enumArg : ENUM_ARGS){
						List<ChoiceNode> args = new ArrayList<ChoiceNode>();
						ChoiceNode intChoice = new ChoiceNode(intArg, intArg);
						intParameter.addChoice(intChoice);
						ChoiceNode stringChoice = new ChoiceNode(stringArg, stringArg);
						stringParameter.addChoice(stringChoice);
						ChoiceNode enumChoice = new ChoiceNode(enumArg, enumArg);
						enumParameter.addChoice(enumChoice);
						args.add(intChoice);
						args.add(stringChoice);
						args.add(enumChoice);

						frameworkMethod.invoke(this, args);

						assertEquals(fResult.arg1, parser.parseValue(intChoice));
						assertEquals(fResult.arg2, parser.parseValue(stringChoice));
						assertEquals(fResult.arg3, parser.parseValue(enumChoice));
					}
				}
			}
		} catch (Throwable e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
}
