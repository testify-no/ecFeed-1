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


public class RootNodeHelper {

	public static String generateNewClassName(RootNode rootNode, String startClassName) {

		if (rootNode.getClassModel(startClassName) == null) {
			return startClassName;
		}

		for (int i = 0;   ; i++) {

			String newClassName = startClassName + String.valueOf(i);
			if (rootNode.getClassModel(newClassName) == null) {
				return newClassName;
			}
		}
	}


}
