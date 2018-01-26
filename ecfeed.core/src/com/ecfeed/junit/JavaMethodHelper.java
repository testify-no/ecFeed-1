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

import java.util.List;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.runner.java.TestMethodInvokerHelper;

public class JavaMethodHelper {

	public static StringBuilder createFailedTestsStringBuilder() {

		return new StringBuilder("\n\nThe following test cases failed:\n");
	}

	public static void appendExceptionMessage(
			String methodName,
			List<ChoiceNode> testData,
			Throwable e,
			StringBuilder stringBuilder) {

		String argumentsDescription = testData.toString();
		String exceptionMessage = getExceptionMessage(e);

		String message = TestMethodInvokerHelper.createErrorMessage(
				methodName, argumentsDescription, exceptionMessage);

		stringBuilder.append(message);
		stringBuilder.append("\n");
	}

	private static String getExceptionMessage(Throwable e) {

		String exceptionMessage = e.getMessage();

		if (exceptionMessage == null) {
			return "TEST FAIL";
		}

		return exceptionMessage;
	}
}
