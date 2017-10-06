/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.widgets.Shell;

import com.ecfeed.utils.EclipseHelper;

public class SaveAsEctDialog {

	public static String open(String filterPath, String originalFileName) {
		return open(filterPath, originalFileName, EclipseHelper.getActiveShell());
	}

	public static String open(String filterPath, String originalFileName, Shell shell) {
		return null; // TODO
	}	

}
