/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.utils.SeleniumHelper;

public class TestRunModeHelper {

	public static TestRunMode getTestRunMode(MethodNode methodNode) {
		ClassNode classNode = methodNode.getClassNode();

		if (classNode.getRunOnAndroid()) {
			return TestRunMode.ANDROID;
		}

		if (SeleniumHelper.isSeleniumRunnerMethod(methodNode)) {
			return TestRunMode.SELENIUM_RUNNER;
		}

		return TestRunMode.JUNIT;
	}

}