/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.JavaUI;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.SystemLogger;


public class ImplementationAdapter {

	public static void goToMethodImplementation(MethodNode methodNode) {

		IMethod method = JavaModelAnalyser.getIMethod(methodNode);

		if (method == null) {
			return;
		}
		
		try {
			JavaUI.openInEditor(method);
		} catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
		}
	}
}
