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
		TYPE_NAME_INT,
		TYPE_NAME_BOOLEAN,
		TYPE_NAME_LONG,
		TYPE_NAME_SHORT,
		TYPE_NAME_BYTE,
		TYPE_NAME_DOUBLE,
		TYPE_NAME_FLOAT,
		TYPE_NAME_CHAR,
		TYPE_NAME_STRING
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

	public static String[] supportedPrimitiveTypes() {
		return SUPPORTED_PRIMITIVE_TYPES;
	}

	public static boolean hasLimitedValuesSet(String type){
		return isJavaType(type) == false || type.equals(getBooleanTypeName());
	}

	public static String getBooleanTypeName(){
		return JavaTypeHelper.TYPE_NAME_BOOLEAN;
	}


}
