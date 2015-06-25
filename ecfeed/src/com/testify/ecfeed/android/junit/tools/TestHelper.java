/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.android.junit.tools;


class ErrorInvalidTestArguments {
	public ErrorInvalidTestArguments() {}
}

public class TestHelper {
	
	public static void invokeTestMethod(Object target, ILogger logger) {
		TestParserInvoker invoker = new TestParserInvoker(logger); 
		invoker.invoke(target, TestArguments.getInstance().getArguments());
	}
	
	public static void prepareTestArguments(String testArguments) {
		TestArguments.prepare(testArguments);
	}
	
	public static Class<?> getClassUnderTest(ILogger loger) {
		
		String arguments = TestArguments.getInstance().getArguments();
		
		if (arguments == null || arguments.isEmpty()) {
			loger.log("There are no arguments required for test.");
			return ErrorInvalidTestArguments.class;
		}
		
		int indexOfSeparator = arguments.indexOf(",");
		
		if (indexOfSeparator == -1) {
			loger.log("Test arguments do not contain class name.");
			return ErrorInvalidTestArguments.class;
		}

		String className = arguments.substring(0, indexOfSeparator).trim();
		
		Class<?> theClass = null;
		try {
			theClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			
			return ErrorInvalidTestArguments.class;
		}
		
		return theClass;
	}
}
