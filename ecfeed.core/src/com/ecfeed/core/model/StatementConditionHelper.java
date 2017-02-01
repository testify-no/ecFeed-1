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

import com.ecfeed.core.utils.StringHelper;

public class StatementConditionHelper {

	public static ChoiceNode getChoiceForMethodParameter(List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {

		if (choices == null) {
			return null;
		}

		MethodNode methodNode = methodParameterNode.getMethod();

		if (methodNode == null) {
			return null;
		}

		int index = methodNode.getParameters().indexOf(methodParameterNode);

		if(choices.size() < index + 1) {
			return null;
		}

		return choices.get(index);
	}
	
	public static String createParameterDescription(String parameterName) {
		return parameterName + "[parameter]";
	}
	
	public static String createLabelDescription(String parameterName) {
		return parameterName + "[label]";
	}	
	
	public static boolean containsTypeInfo(String string, String typeDescription) {

		if (!(string.contains("["))) {
			return false;
		}

		if (!(string.contains("]"))) {
			return false;
		}		

		if (typeDescription != null) {
			if (!string.contains("[" + typeDescription + "]")) {
				return false;
			}
		}

		return true;
	}

	public static String removeTypeInfo(String string, String typeDescription) {
		return StringHelper.removeFromPostfix("[" + typeDescription + "]", string);
	}
	

}

