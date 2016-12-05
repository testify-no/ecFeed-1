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


public class NodePropertyDefFindByType {

	private static final String UNMAPPED = "Unmapped";
	private static final String ID = "Id";
	private static final String CLASS_NAME = "Class name";
	private static final String TAG_NAME = "Tag name";
	private static final String NAME = "Name";
	private static final String LINK_TEXT = "Link text";
	private static final String PARTIAL_LINK_TEXT = "Partial link text";
	private static final String CSS_SELECTOR = "CSS selector";
	private static final String XPATH = "Xpath";

	public static NodePropertyDef findByTypeOfElement = 
			new NodePropertyDef(
					"findByTypeOfElement", JavaTypeHelper.TYPE_NAME_STRING, UNMAPPED,
					new String[]{ UNMAPPED, ID, CLASS_NAME, TAG_NAME, NAME, LINK_TEXT, PARTIAL_LINK_TEXT, CSS_SELECTOR, XPATH });

	public static boolean isUnmapped(String value) {
		if (value.equals(UNMAPPED)) {
			return true;
		}
		return false;
	}	

	public static boolean isId(String value) {
		if (value.equals(ID)) {
			return true;
		}
		return false;
	}	

	public static boolean isClassName(String value) {
		if (value.equals(CLASS_NAME)) {
			return true;
		}
		return false;
	}	

	public static boolean isTagName(String value) {
		if (value.equals(TAG_NAME)) {
			return true;
		}
		return false;
	}	

	public static boolean isName(String value) {
		if (value.equals(NAME)) {
			return true;
		}
		return false;
	}	

	public static boolean isLinkText(String value) {
		if (value.equals(LINK_TEXT)) {
			return true;
		}
		return false;
	}	

	public static boolean isPartialLinkText(String value) {
		if (value.equals(PARTIAL_LINK_TEXT)) {
			return true;
		}
		return false;
	}	

	public static boolean isCssSelector(String value) {
		if (value.equals(CSS_SELECTOR)) {
			return true;
		}
		return false;
	}	

	public static boolean isXPath(String value) {
		if (value.equals(XPATH)) {
			return true;
		}
		return false;
	}	

}
