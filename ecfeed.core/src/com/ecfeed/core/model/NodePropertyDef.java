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


class NodePropertyDef {
	private String fName;
	private String fType;
	private String fDefaultValue;
	private String[] fPossibleValues;

	NodePropertyDef(String name, String type, String defaultValue, String[] possibleValues) {
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

	boolean matchesPossibleValue(String valueToMatch) {
		for (String possibleValue : fPossibleValues) {
			if (possibleValue.equals(valueToMatch)) {
				return true;
			}
		}

		return false;
	}
}
