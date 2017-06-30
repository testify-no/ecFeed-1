/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common.local;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.CommonConstants;

public class JavaCodeEnumHelper {

	public static List<String> enumValues(String typeName) {

		IType type = JavaModelAnalyser.getIType(typeName);
		List<String> result = new ArrayList<String>();

		try {
			if(type != null && type.isEnum()){
				String typeSignature = Signature.createTypeSignature(type.getElementName(), false);
				try {
					if(type.isEnum()){
						for(IField field : type.getFields()){
							if(field.getTypeSignature().equals(typeSignature)){
								result.add(field.getElementName());
							}
						}
					}
				} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
				return result;
			}
		} catch (JavaModelException e) {SystemLogger.logCatch(e.getMessage());}
		return new ArrayList<String>();
	}

	public static String defaultEnumExpectedValue(String type) {

		String value = CommonConstants.DEFAULT_EXPECTED_ENUM_VALUE;

		List<String> enumValues = enumValues(type);
		if(enumValues.size() > 0){
			value = enumValues.get(0);
		}
		return value;
	}

}
