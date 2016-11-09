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


public class NodePropertyDescriptions {

	static class PropertyDescription {
		private String fName;
		private String fType;

		PropertyDescription(String name, String type) {
			fName = name;
			fType = type;
		}

		String getName() {
			return fName;
		}

		String getType() {
			return fType;
		}
	}

	public enum PropertyId {
		RUN_ON_ANDROID(0),
		ANDROID_RUNNER(1),
		METHOD_RUNNER(2);

		private final int fValue; 

		private PropertyId(int value) {
			fValue = value;
		}

		private final int getValue() {
			return fValue;
		}
	}

	static PropertyDescription[] propertyDescriptions = 
		{
		new PropertyDescription("runOnAndroid", JavaTypeHelper.TYPE_NAME_BOOLEAN),
		new PropertyDescription("androidRunner", JavaTypeHelper.TYPE_NAME_STRING),
		new PropertyDescription("methodRunner", JavaTypeHelper.TYPE_NAME_STRING),
		};

	public static String getPropertyName(PropertyId propertyId) {
		return getDescription(propertyId).getName();
	}

	public static String getPropertyType(PropertyId propertyId) {
		return getDescription(propertyId).getType();
	}	

	private static PropertyDescription getDescription(PropertyId propertyId) {
		return propertyDescriptions[propertyId.getValue()];
	}

}
