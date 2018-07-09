/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.utils.JavaTypeHelper;

public class EclipseTypeHelper {


	public static List<String> getSpecialValues(String typeName) {

		List<String> result = new ArrayList<String>();

		switch(typeName){
		case JavaTypeHelper.TYPE_NAME_BOOLEAN:
			result.addAll(Arrays.asList(JavaTypeHelper.SPECIAL_VALUES_FOR_BOOLEAN));
			break;
		case JavaTypeHelper.TYPE_NAME_CHAR:
			result.addAll(Arrays.asList(JavaTypeHelper.DEFAULT_EXPECTED_CHAR_VALUE));
			break;
		case JavaTypeHelper.TYPE_NAME_BYTE:
		case JavaTypeHelper.TYPE_NAME_INT:
		case JavaTypeHelper.TYPE_NAME_LONG:
		case JavaTypeHelper.TYPE_NAME_SHORT:
			result.addAll(Arrays.asList(JavaTypeHelper.SPECIAL_VALUES_FOR_INTEGER));
			break;
		case JavaTypeHelper.TYPE_NAME_DOUBLE:
		case JavaTypeHelper.TYPE_NAME_FLOAT:
			result.addAll(Arrays.asList(JavaTypeHelper.SPECIAL_VALUES_FOR_FLOAT));
			break;
		case JavaTypeHelper.TYPE_NAME_STRING:
			result.addAll(Arrays.asList(com.ecfeed.core.utils.CommonConstants.STRING_SPECIAL_VALUES));
			break;
		default:
			result.addAll(JavaCodeEnumHelper.enumValues(typeName));
			break;
		}
		return result;
	}


	public static String getDefaultExpectedValue(String type) {
		switch(type){
		case JavaTypeHelper.TYPE_NAME_BYTE:
			return JavaTypeHelper.DEFAULT_EXPECTED_BYTE_VALUE;
		case JavaTypeHelper.TYPE_NAME_BOOLEAN:
			return JavaTypeHelper.DEFAULT_EXPECTED_BOOLEAN_VALUE;
		case JavaTypeHelper.TYPE_NAME_CHAR:
			return JavaTypeHelper.DEFAULT_EXPECTED_CHAR_VALUE;
		case JavaTypeHelper.TYPE_NAME_DOUBLE:
			return JavaTypeHelper.DEFAULT_EXPECTED_DOUBLE_VALUE;
		case JavaTypeHelper.TYPE_NAME_FLOAT:
			return JavaTypeHelper.DEFAULT_EXPECTED_FLOAT_VALUE;
		case JavaTypeHelper.TYPE_NAME_INT:
			return JavaTypeHelper.DEFAULT_EXPECTED_INT_VALUE;
		case JavaTypeHelper.TYPE_NAME_LONG:
			return JavaTypeHelper.DEFAULT_EXPECTED_LONG_VALUE;
		case JavaTypeHelper.TYPE_NAME_SHORT:
			return JavaTypeHelper.DEFAULT_EXPECTED_SHORT_VALUE;
		case JavaTypeHelper.TYPE_NAME_STRING:
			return JavaTypeHelper.DEFAULT_EXPECTED_STRING_VALUE;
		default:
			return JavaCodeEnumHelper.defaultEnumExpectedValue(type);
		}
	}

}
