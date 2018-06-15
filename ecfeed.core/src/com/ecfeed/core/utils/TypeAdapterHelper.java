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

import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;

public class TypeAdapterHelper {

	public static final String USER_TYPE = "USER_TYPE";

	public static final String[] TYPES_CONVERTABLE_TO_NUMBERS = new String[]{
		JavaTypeHelper.TYPE_NAME_INT, 
		JavaTypeHelper.TYPE_NAME_FLOAT, 
		JavaTypeHelper.TYPE_NAME_DOUBLE, 
		JavaTypeHelper.TYPE_NAME_LONG, 
		JavaTypeHelper.TYPE_NAME_SHORT, 
		JavaTypeHelper.TYPE_NAME_STRING, 
		JavaTypeHelper.TYPE_NAME_BYTE, 
		JavaTypeHelper.TYPE_NAME_CHAR
	};

	public static void reportRuntimeExceptionCannotConvert(String value, String typeName) {

		final String CANNOT_CONVERT_VALUE = "Cannot convert value [" + value + "] to " + typeName + ".";
		ExceptionHelper.reportRuntimeException(CANNOT_CONVERT_VALUE);
	}

	public static String handleConversionError(String value, String defaultValue, EConversionMode conversionMode) {

		if (conversionMode == EConversionMode.QUIET) {
			return defaultValue;
		}

		TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaTypeHelper.TYPE_NAME_SHORT);
		return null;
	}
}
