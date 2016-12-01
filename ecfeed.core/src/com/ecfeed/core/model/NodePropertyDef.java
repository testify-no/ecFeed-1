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
	NodePropertyValueSet fValueSet;

	NodePropertyDef(String name, String type, String defaultValue, String[] possibleValues) {
		fName = name;
		fType = type;
		fValueSet = new NodePropertyValueSet(defaultValue, possibleValues);
	}

	String getName() {
		return fName;
	}

	String getType() {
		return fType;
	}

	String getDefaultValue() {
		return fValueSet.getDefaultValue();
	}	

	String[] getPossibleValues() {
		return fValueSet.getPossibleValues();
	}

	boolean matchesPossibleValue(String valueToMatch) {
		return fValueSet.isOneOfPossibleValues(valueToMatch);
	}
}
