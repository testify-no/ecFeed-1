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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.adapter.java.AdapterConstants;
import com.ecfeed.core.adapter.java.Messages;


public class MethodNodeHelper {

	public static String simplifiedToString(MethodNode method) {
		
		String result = method.toString();
		
		for (AbstractParameterNode parameter : method.getParameters()) {
			String type = parameter.getType();
			String newType = ModelHelper.convertToLocalName(type);
			result = result.replaceAll(type, newType);
		}
		
		return result;
	}
	
	public static boolean validateMethodName(String name) {
		
		return validateMethodName(name, null);
	}

	public static boolean validateMethodName(String name, List<String> problems) {
		
		boolean valid = name.matches(AdapterConstants.REGEX_METHOD_NODE_NAME);
		valid &= Arrays.asList(AdapterConstants.JAVA_KEYWORDS).contains(name) == false;
		
		if(valid == false){
			if(problems != null){
				problems.add(Messages.METHOD_NAME_REGEX_PROBLEM);
			}
		}
		return valid;
	}
	
	public static List<String> getArgNames(MethodNode method) {
		
		List<String> result = new ArrayList<String>();
		
		for(AbstractParameterNode parameter : method.getParameters()){
			result.add(parameter.getName());
		}
		
		return result;
	}
	
	public static List<String> getArgTypes(MethodNode method) {
		
		List<String> result = new ArrayList<String>();
		
		for (AbstractParameterNode parameter : method.getParameters()) {
			result.add(parameter.getType());
		}
		
		return result;
	}
	
}
