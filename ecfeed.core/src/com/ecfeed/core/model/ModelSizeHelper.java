/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.List;

public class ModelSizeHelper {

	static final int MAX_PARAMETER_COUNT_FOR_FREE_VERSION = 7; 
	static final int MAX_CHOICE_COUNT_FOR_FREE_VERSION = 7;

	public static String isMethodOkForFreeUse(MethodNode methodNode) {

		if (methodNode.getParametersCount() > MAX_PARAMETER_COUNT_FOR_FREE_VERSION) {
			return "Method has too many parameters for non-commercial use. Max " + 
					MAX_PARAMETER_COUNT_FOR_FREE_VERSION + " allowed.";
		}

		return numberOfChoicesOk(methodNode);
	}

	private static String numberOfChoicesOk(MethodNode methodNode) {

		List<AbstractParameterNode> parameters = methodNode.getParameters();

		for (AbstractParameterNode node : parameters) {
			if (node.getChoiceCount() > MAX_CHOICE_COUNT_FOR_FREE_VERSION) {
				return "Parameter " + node.getName() + 
						" has too many choices for non-commercial use. Max " + 
						MAX_CHOICE_COUNT_FOR_FREE_VERSION + " allowed.";
			}
		}

		return null;
	}

}
