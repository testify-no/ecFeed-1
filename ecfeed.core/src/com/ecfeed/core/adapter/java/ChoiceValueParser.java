/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.java;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.JavaTypeHelper;

public class ChoiceValueParser {
	private ModelClassLoader fLoader;
	boolean fIsExport;

	public ChoiceValueParser(ModelClassLoader loader, boolean isExport){

		fLoader = loader;
		fIsExport = isExport;
	}

	private Object parseUserTypeValue(String valueString, String typeName) {

		Object value = null;
		Class<?> typeClass = fLoader.loadClass(typeName);

		if (typeClass != null) {
			for (Object object: typeClass.getEnumConstants()) {
				if ((((Enum<?>)object).name()).equals(valueString)) {
					value = object;
					break;
				}
			}
		}

		return value;
	}

	public Object parseValue(ChoiceNode choice){

		if(choice.getParameter() != null){
			return parseValue(choice.getValueString(), choice.getParameter().getType());
		}
		return null;
	}

	public Object parseValue(String valueString, String typeName) {

		if(typeName == null || valueString == null){
			return null;
		}
		if (JavaTypeHelper.isJavaType(typeName)) {
			return JavaTypeHelper.parseJavaType(valueString, typeName); 
		}
		if (fIsExport) {
			return valueString;
		}

		return parseUserTypeValue(valueString, typeName);
	}

}
