/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class SetupDialogExecuteOnline extends SetupDialogOnline {


	public static SetupDialogExecuteOnline create(
			Shell parentShell, 
			MethodNode method,
			IJavaProjectProvider javaProjectProvider, 
			String targetFile) {

		if (!canCreate(method)) {
			return null;
		}

		return new SetupDialogExecuteOnline(
				parentShell, 
				method,
				javaProjectProvider, 
				targetFile);				
	}

	protected SetupDialogExecuteOnline(
			Shell parentShell, 
			MethodNode method,
			IJavaProjectProvider javaProjectProvider, 
			String targetFile) {
		super(parentShell, method, true, javaProjectProvider, null, targetFile);
	}

	@Override
	protected String getDialogTitle() {
		final String DIALOG_EXECUTE_ONLINE_TITLE = "Execute online test";
		return DIALOG_EXECUTE_ONLINE_TITLE;
	}

	@Override
	protected int getContent() {
		return CONSTRAINTS_COMPOSITE | CHOICES_COMPOSITE
				| GENERATOR_SELECTION_COMPOSITE;
	}

}
