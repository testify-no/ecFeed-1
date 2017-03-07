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

import com.ecfeed.core.adapter.java.AdapterConstants;


public class JavaLanguageHelper {

	public static boolean isJavaKeyword(String word){
		return Arrays.asList(AdapterConstants.JAVA_KEYWORDS).contains(word);
	}

	public static boolean isValidJavaIdentifier(String value) {
		return (value.matches(AdapterConstants.REGEX_JAVA_IDENTIFIER) && JavaLanguageHelper.isJavaKeyword(value) == false);
	}
	
	public static String[] javaKeywords(){
		return AdapterConstants.JAVA_KEYWORDS;
	}
	
}
