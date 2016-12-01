/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.util.Arrays;

public class JavaTypeHelper {

	public static final String TYPE_NAME_BOOLEAN = "boolean";
	public static final String TYPE_NAME_BYTE = "byte";
	public static final String TYPE_NAME_CHAR = "char";
	public static final String TYPE_NAME_DOUBLE = "double";
	public static final String TYPE_NAME_FLOAT = "float";
	public static final String TYPE_NAME_INT = "int";
	public static final String TYPE_NAME_LONG = "long";
	public static final String TYPE_NAME_SHORT = "short";
	public static final String TYPE_NAME_STRING = "String";

	private static final String[] SUPPORTED_PRIMITIVE_TYPES = new String[]{
		TYPE_NAME_STRING,
		TYPE_NAME_CHAR,
		TYPE_NAME_BOOLEAN,
		TYPE_NAME_BYTE,
		TYPE_NAME_INT,
		TYPE_NAME_SHORT,
		TYPE_NAME_LONG,
		TYPE_NAME_FLOAT,
		TYPE_NAME_DOUBLE
	};

	public static boolean isJavaType(String typeName) {
		return Arrays.asList(SUPPORTED_PRIMITIVE_TYPES).contains(typeName);
	}

	public static boolean isUserType(String typeName) {
		if (isJavaType(typeName)) {
			return false;
		}
		return true;
	}

	public static String[] getSupportedJavaTypes() {
		return SUPPORTED_PRIMITIVE_TYPES;
	}

	public static boolean hasLimitedValuesSet(String type){
		return isJavaType(type) == false || type.equals(getBooleanTypeName());
	}

	public static String getBooleanTypeName(){
		return JavaTypeHelper.TYPE_NAME_BOOLEAN;
	}

	public static boolean isStringTypeName(String typeName) {
		if (typeName.equals(TYPE_NAME_STRING)) {
			return true;
		}
		return false;
	}
	
	public static boolean isCharTypeName(String typeName) {
		if (typeName.equals(TYPE_NAME_CHAR)) {
			return true;
		}
		return false;
	}	
	
	public static boolean isBooleanTypeName(String typeName) {
		if (typeName.equals(TYPE_NAME_BOOLEAN)) {
			return true;
		}
		return false;
	}	
	
	public static boolean isByteTypeName(String typeName) {
		if (typeName.equals(TYPE_NAME_BYTE)) {
			return true;
		}
		return false;
	}	
	
	public static boolean isIntTypeName(String typeName) {
		if (typeName.equals(TYPE_NAME_INT)) {
			return true;
		}
		return false;
	}	
	
	public static boolean isShortTypeName(String typeName) {
		if (typeName.equals(TYPE_NAME_SHORT)) {
			return true;
		}
		return false;
	}	
	
	public static boolean isFloatTypeName(String typeName) {
		if (typeName.equals(TYPE_NAME_FLOAT)) {
			return true;
		}
		return false;
	}	
	
	public static boolean isDoubleTypeName(String typeName) {
		if (typeName.equals(TYPE_NAME_FLOAT)) {
			return true;
		}
		return false;
	}	
	
	public static boolean isNumericTypeName(String typeName) {

		if (isByteTypeName(typeName)) {
			return true;
		}
		if (isIntTypeName(typeName)) {
			return true;
		}
		if (isShortTypeName(typeName)) {
			return true;
		}
		if (isFloatTypeName(typeName)) {
			return true;
		}
		if (isDoubleTypeName(typeName)) {
			return true;
		}
		return false;
	}


}
