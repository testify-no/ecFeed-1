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
	private static final String PAGE_URL = "Page URL";
	private static final String DELAY = "Delay";
	private static final String BROWSER = "Browser";

	public static NodePropertyDef parameterType = 
			new NodePropertyDef(
					PARAMETER_TYPE, JavaTypeHelper.TYPE_NAME_STRING, UNDEFINED,
					new String[]{UNDEFINED, PAGE_ELEMENT, PAGE_URL, DELAY, BROWSER });


	public void getPossibleTypes(String parameterType) {
		if (JavaTypeHelper.isStringTypeName(parameterType)) {

		}
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
