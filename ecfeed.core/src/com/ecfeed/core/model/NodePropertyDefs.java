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


public class NodePropertyDefs {

	static class PropertyDef {
		private String fName;
		private String fType;
		private String fDefaultValue;
		private String[] fPossibleValues;

		PropertyDef(String name, String type, String defaultValue, String[] possibleValues) {
			fName = name;
			fType = type;
			fDefaultValue = defaultValue;
			fPossibleValues = possibleValues;
		}

		String getName() {
			return fName;
		}

		String getType() {
			return fType;
		}

		String getDefaultValue() {
			return fDefaultValue;
		}	

		String[] getPossibleValues() {
			return fPossibleValues;
		}
	}

	public enum PropertyId {
		RUN_ON_ANDROID(0),
		ANDROID_RUNNER(1),
		METHOD_RUNNER(2);

		private final int fIndex; 

		private PropertyId(int index) {
			fIndex = index;
		}

		private final int getIndex() {
			return fIndex;
		}
	}

	static PropertyDef[] fPropertyDefs = 
		{
		new PropertyDef(
				"runOnAndroid", JavaTypeHelper.TYPE_NAME_BOOLEAN, "false", new String[]{"false", "true"}),

				new PropertyDef(
						"androidRunner", JavaTypeHelper.TYPE_NAME_STRING, null, null),

						new PropertyDef(
								"methodRunner", JavaTypeHelper.TYPE_NAME_STRING, "Java Runner", 
								new String[]{"Java Runner", "Web Driver"}),
		};

	public static String getPropertyName(PropertyId propertyId) {
		return getDefinition(propertyId).getName();
	}

	public static String getPropertyType(PropertyId propertyId) {
		return getDefinition(propertyId).getType();
	}	

	public static String getPropertyDefaultValue(PropertyId propertyId) {
		return getDefinition(propertyId).getDefaultValue();
	}	

	public static String[] getPropertyPossibleValues(PropertyId propertyId) {
		return getDefinition(propertyId).getPossibleValues();
	}	

	private static PropertyDef getDefinition(PropertyId propertyId) {
		return fPropertyDefs[propertyId.getIndex()];
	}

}
