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

import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.adapter.java.AdapterConstants;


public class ModelHelper {
	
	public static String convertToLocalName(String qualifiedName) {
		
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		
		if (lastDotIndex == -1) {
			return qualifiedName;
		}
		
		return qualifiedName.substring(lastDotIndex + 1);
	}

	public static String getQualifiedName(String packageName, String localName) {
		
		return packageName + "." + localName;
	}
	
	public static String getPackageName(String qualifiedName) {
		
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		
		return (lastDotIndex == -1)? "" : qualifiedName.substring(0, lastDotIndex);
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
	
	public static String convertParameterToSimplifiedString(AbstractParameterNode parameter) {
		
		String result = parameter.toString();
		String type = parameter.getType();
		
		result.replace(type, ModelHelper.convertToLocalName(type));
		
		return result;
	}
	
	public static String convertParameterToSimpleModeString(AbstractParameterNode parameter) {
		
		String result = parameter.toString();
		String type = parameter.getType();
		String simpleType = convertTypeToSimpleMode(type);
		return result.replace(type, ModelHelper.convertToLocalName(simpleType));
	}

	public static String convertTypeToSimpleMode(String type) {
		List<String> numericList = Arrays.asList("int", "double", "float", "long", "short", "byte");

		if (numericList.contains(type)) {
			return "number";
		}

		if (type.equals("String")) {
			return "text";
		}

		if (type.equals("boolean")) {
			return "boolean";
		}

		return "text";
	}
	
}
