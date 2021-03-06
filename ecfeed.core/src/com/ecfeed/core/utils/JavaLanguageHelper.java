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

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import com.ecfeed.core.adapter.java.AdapterConstants;


public class JavaLanguageHelper {

	public static final String[] JAVA_KEYWORDS = new String[]
			{ "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do",
		"if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public",
		"throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char",
		"final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float",
		"native", "super", "while", "null", "true", "false" };
	
	public static boolean isJavaKeyword(String word) {

		return Arrays.asList(JAVA_KEYWORDS).contains(word);
	}

	public static boolean isValidJavaIdentifier(String value) {

		if (!value.matches(AdapterConstants.REGEX_JAVA_IDENTIFIER)) {
			return false;
		}
		
		if (JavaLanguageHelper.isJavaKeyword(value)) {
			return false;
		}
			
		return true;
	}

	public static String[] getJavaKeywords() {

		return JAVA_KEYWORDS;
	}

	public static boolean isValidTypeName(String name) {

		if (name == null) {
			return false;
		}

		if (JavaTypeHelper.isJavaType(name)) { 
			return true;
		}

		if (name.matches(AdapterConstants.REGEX_CLASS_NODE_NAME) == false) {
			return false;
		}

		StringTokenizer tokenizer = new StringTokenizer(name, ".");

		while (tokenizer.hasMoreTokens()) {

			String segment = tokenizer.nextToken();

			if(JavaLanguageHelper.isValidJavaIdentifier(segment) == false) {
				return false;
			}
		}
		return true;
	}	

	public static List<String> getEnumValuesNames(URLClassLoader loader, String enumTypeName) {
		
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
	
}
