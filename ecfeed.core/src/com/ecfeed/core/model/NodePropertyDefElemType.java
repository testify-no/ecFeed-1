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

import com.ecfeed.core.utils.JavaTypeHelper;


public class NodePropertyDefElemType {

	private static final String PARAMETER_TYPE = "parameterType";
	private static final String UNDEFINED = "Undefined";
	private static final String PAGE_ELEMENT = "Page element";
	private static final String TEXT = "Text";
	private static final String PAGE_URL = "Page URL";
	private static final String DELAY = "Delay";
	private static final String BROWSER = "Browser";

	public static NodePropertyDef parameterType = 
			new NodePropertyDef(
					PARAMETER_TYPE, JavaTypeHelper.TYPE_NAME_STRING, UNDEFINED,
					new String[]{UNDEFINED, TEXT, PAGE_ELEMENT, PAGE_URL, DELAY, BROWSER });

	private static String[] TAB_BASIC= new String[] {TEXT, PAGE_ELEMENT};

	public static String[] getPossibleValues(String parameterType) {
		
		if (JavaTypeHelper.isStringTypeName(parameterType)) {
			return new String[] {TEXT, PAGE_ELEMENT, PAGE_URL, BROWSER};
		}
		if (JavaTypeHelper.isBooleanTypeName(parameterType)) {
			return new String[] {PAGE_ELEMENT};
		}		
		if (	JavaTypeHelper.isCharTypeName(parameterType) ||
				JavaTypeHelper.isBooleanTypeName(parameterType) ||
				JavaTypeHelper.isNumericTypeName(parameterType) ) {
			return TAB_BASIC;
		}
		return null;
	}
	
	public static String getDefaultValue(String parameterType) {
		
		if (JavaTypeHelper.isBooleanTypeName(parameterType)) {
			return PAGE_ELEMENT;
		}		
		if (	JavaTypeHelper.isStringTypeName(parameterType) ||
				JavaTypeHelper.isCharTypeName(parameterType) ||
				JavaTypeHelper.isBooleanTypeName(parameterType) ||
				JavaTypeHelper.isNumericTypeName(parameterType) ) {
			return TEXT;
		}
		return null;
	}

	public static boolean isUndefined(String value) {
		if (value.equals(UNDEFINED)) {
			return true;
		}
		return false;
	}
	
	public static boolean isPageElement(String value) {
		if (value.equals(PAGE_ELEMENT)) {
			return true;
		}
		return false;
	}

	public static boolean isPageUrl(String value) {
		if (value.equals(PAGE_URL)) {
			return true;
		}
		return false;
	}	

	public static boolean isDelay(String value) {
		if (value.equals(DELAY)) {
			return true;
		}
		return false;
	}

	public static boolean isBrowser(String value) {
		if (value.equals(BROWSER)) {
			return true;
		}
		return false;
	}

}
