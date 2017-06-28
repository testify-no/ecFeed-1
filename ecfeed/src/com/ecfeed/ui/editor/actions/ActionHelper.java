/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.actions;

import com.ecfeed.core.utils.SystemHelper;

public class ActionHelper {
	
	public static String addShortcut(String actionName, String shortcut) {

		if (shortcut == null) {
			return actionName;
		}

		if (SystemHelper.isOperatingSystemMacOs()) {
			addShortcutWithParentheses(actionName, shortcut);	
		}
		
		return actionName + "\t" + shortcut;
	}
	
	public static String addShortcutWithParentheses(String actionName, String shortcut) {
		return actionName + "  (" + shortcut + ")";
	}

}