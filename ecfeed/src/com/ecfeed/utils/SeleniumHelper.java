/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.utils;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.NodePropertyDefs;


public class SeleniumHelper {

	public static boolean isSeleniumRunnerMethod(MethodNode methodNode) {

		if (methodNode == null) {
			return false;
		}

		String value = methodNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER);

		return NodePropertyDefs.isWebRunnerMethod(value);
	}	

}
