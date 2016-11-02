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


public class Attribute{
	private String fType;
	private String fName;
	
	public Attribute(String type, String name) {
		fType = type;
		fName = name;
	}
	
	public String getType() {
		return fType;
	}
	
	public String getName() {
		return fName;
	}
}
