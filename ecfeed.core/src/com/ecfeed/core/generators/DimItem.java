/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

public class DimItem {

	String fDimension; // e.g. index of method parameter
	String fItem;

	public DimItem(String dimension, String item) {
		fDimension = dimension;
		fItem = item;
	}
	String getDim(){
		return fDimension;
	}
	String getItem(){
		return fItem;
	}

}
