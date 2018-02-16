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
import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.runner.Messages;
import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.core.utils.EcException;

public class OnlineRunnerMethod extends AbstractFrameworkMethod{

	IGenerator<ChoiceNode> fGenerator;

	public OnlineRunnerMethod(
			Method method, 
			IGenerator<ChoiceNode> initializedGenerator, 
			ModelClassLoader loader) throws RunnerException {

		super(method, loader);
		fGenerator = initializedGenerator;
	}

	@Override
	public Object invokeExplosively(Object target, Object... p) throws Throwable {

		int totalTestCaseCounter = 0;
		int failedTestCaseCounter = 0;
		StringBuilder stringBuilder = new StringBuilder();
		List<ChoiceNode> listOfChoices = new ArrayList<>();

		for (;;) {

			try {
				listOfChoices = fGenerator.next();

			} catch (Exception e){
				RunnerException.report(Messages.RUNNER_EXCEPTION(e.getMessage()));
			}

			if (listOfChoices == null) {
				break;
			}

			totalTestCaseCounter++;

			try {
				super.invoke(target, listOfChoices);

			} catch (Throwable e) {

				if (failedTestCaseCounter == 0) {
					JavaMethodHelper.appendFailedTestsMessage(stringBuilder);
				}

				failedTestCaseCounter++;
				JavaMethodHelper.appendExceptionMessage(getName(), listOfChoices, e, stringBuilder);
			}

		}

		JavaMethodHelper.addTestStatistics(totalTestCaseCounter, failedTestCaseCounter, stringBuilder);

		if (failedTestCaseCounter > 0) {
			EcException.report(stringBuilder.toString());
		}

		return null;
	}

}
