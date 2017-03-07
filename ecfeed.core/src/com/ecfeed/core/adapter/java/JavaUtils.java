/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.java;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelHelper;

public class JavaUtils {

	public static List<String> enumValuesNames(URLClassLoader loader, String enumTypeName){
		List<String> values = new ArrayList<String>();
		try {
			Class<?> enumType = loader.loadClass(enumTypeName);
			if(enumType != null && enumType.isEnum()){
				for (Object object: enumType.getEnumConstants()) {
					values.add(((Enum<?>)object).name());
				}
			}
		} catch (ClassNotFoundException e) {
		}
		return values;
	}

	public static boolean isValidTestCaseName(String name) {
		return name.matches(AdapterConstants.REGEX_TEST_CASE_NODE_NAME);
	}

	public static boolean isValidConstraintName(String name) {
		return name.matches(AdapterConstants.REGEX_CONSTRAINT_NODE_NAME);
	}

	public static boolean validateTestCaseName(String name){
		return name.matches(AdapterConstants.REGEX_TEST_CASE_NODE_NAME);
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
		for(AbstractParameterNode parameter : method.getParameters()){
			result.add(parameter.getType());
		}
		return result;
	}

	public static String simplifiedToString(AbstractParameterNode parameter){
		String result = parameter.toString();
		String type = parameter.getType();
		result.replace(type, ModelHelper.convertToLocalName(type));
		return result;
	}

}
