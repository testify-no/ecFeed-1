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
	private static final String PAGE_ELEMENT = "Page element";
	private static final String BUTTON = "Button";
	private static final String TEXT = "Text";
	private static final String PAGE_URL = "Page URL";
	private static final String DELAY = "Delay";
	private static final String BROWSER = "Browser";
	private static final String EMPTY_STR = "";
	private static final String UNMAPPED = "Unmapped";

	public static NodePropertyDef parameterType = 
			new NodePropertyDef(
					PARAMETER_TYPE, JavaTypeHelper.TYPE_NAME_STRING, EMPTY_STR,
					new String[]{EMPTY_STR, TEXT, PAGE_ELEMENT, PAGE_URL, DELAY, BROWSER });


	private static NodePropertyValueSet VALUE_SET_FOR_STRING = 
			new NodePropertyValueSet(UNMAPPED, new String[] {UNMAPPED, TEXT, PAGE_ELEMENT, PAGE_URL, BROWSER});

	private static NodePropertyValueSet VALUE_SET_FOR_CHAR = 
			new NodePropertyValueSet(UNMAPPED, new String[] {UNMAPPED, TEXT, PAGE_ELEMENT});

	private static NodePropertyValueSet VALUE_SET_FOR_BOOLEAN = 
			new NodePropertyValueSet(UNMAPPED, new String[] {UNMAPPED, BUTTON, PAGE_ELEMENT});

	private static NodePropertyValueSet VALUE_SET_FOR_NUMERIC_TYPES = 
			new NodePropertyValueSet(UNMAPPED, new String[] {UNMAPPED, TEXT, DELAY, PAGE_ELEMENT});	

	public static String[] getPossibleValues(String parameterType) {
		return getValueSet(parameterType).getPossibleValues();
	}

	public static String getDefaultValue(String parameterType) {
		if (parameterType == null) {
			return null;
		}
		return getValueSet(parameterType).getDefaultValue();
	}

	private static NodePropertyValueSet getValueSet(String parameterType) {
		if (JavaTypeHelper.isStringTypeName(parameterType)) {
			return VALUE_SET_FOR_STRING;
		}
		if (JavaTypeHelper.isCharTypeName(parameterType)) {
			return VALUE_SET_FOR_CHAR;
		}
		if (JavaTypeHelper.isBooleanTypeName(parameterType)) {
			return VALUE_SET_FOR_BOOLEAN;
		}		
		if (JavaTypeHelper.isNumericTypeName(parameterType)) {
			return VALUE_SET_FOR_NUMERIC_TYPES;
		}		
		return null;
	}

	public static boolean isText(String value) {
		if (value.equals(TEXT)) {
			return true;
		}
		return false;
	}

	public static boolean isButton(String value) {
		if (value.equals(BUTTON)) {
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

	public static boolean isChildElementAvailable(String value) {
		if (isPageElement(value) || isText(value) || isButton(value)) {
			return true;
		}
		return false;
	}

	public static boolean isActionAvailable(String value) {
		if (isPageElement(value)) {
			return true;
		}
		return false;
	}	
}
