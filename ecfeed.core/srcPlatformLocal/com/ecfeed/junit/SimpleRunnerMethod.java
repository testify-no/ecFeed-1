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
import java.util.List;

import com.ecfeed.core.adapter.java.ModelClassLoader;
import com.ecfeed.core.model.ChoiceNode;

public class SimpleRunnerMethod extends AbstractFrameworkMethod {

	private List<ChoiceNode> fListOfChoices;

	public SimpleRunnerMethod(Method method, List<ChoiceNode> listOfChoiceNodes, ModelClassLoader loader) {

		super(method, loader);
		fListOfChoices = listOfChoiceNodes;
	}

	@Override
	public Object invokeExplosively(Object target, Object... parameters) throws Throwable{

		super.invoke(target, fListOfChoices);
		return null;
	}

	@Override
	public String getName() {

		return super.getName() + "[ " + createParametersDescription(fListOfChoices) + " ]";
	}

	private String createParametersDescription(List<ChoiceNode> fListOfChoices) {

		StringBuilder stringBuilder = new StringBuilder();
		boolean firstTime = true;

		for (ChoiceNode choiceNode : fListOfChoices) {

			if (!firstTime) {
				stringBuilder.append(", ");
			}

			firstTime = false;

			stringBuilder.append(choiceNode.toStringWithParenthesis());
		}

		return stringBuilder.toString();
	}

}