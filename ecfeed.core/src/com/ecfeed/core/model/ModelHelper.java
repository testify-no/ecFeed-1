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


public class ModelHelper {
	
	public static String convertToLocalName(String qualifiedName) {
		
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		
		if (lastDotIndex == -1) {
			return qualifiedName;
		}
		
		return qualifiedName.substring(lastDotIndex + 1);
	}

	public static String getQualifiedName(String packageName, String localName){
		return packageName + "." + localName;
	}
	
}
