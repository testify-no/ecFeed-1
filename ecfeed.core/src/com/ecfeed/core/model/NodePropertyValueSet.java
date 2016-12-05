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

class NodePropertyValueSet {
	private String fDefaultValue;
	private String[] fPossibleValues;

	NodePropertyValueSet(String defaultValue, String[] possibleValues) {
		fDefaultValue = defaultValue;
		fPossibleValues = possibleValues;
	}

	String getDefaultValue() {
		return fDefaultValue;
	}	

	String[] getPossibleValues() {
		return fPossibleValues;
	}

	boolean isOneOfPossibleValues(String valueToMatch) {
		for (String possibleValue : fPossibleValues) {
			if (possibleValue.equals(valueToMatch)) {
				return true;
			}
		}

		return false;
	}
}

