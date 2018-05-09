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
import com.ecfeed.core.serialization.export.ExportTemplateFactory;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class SetupDialogExportOnline extends SetupDialogOnline {

	public static SetupDialogExportOnline create(
			Shell parentShell, 
			MethodNode method,
			IJavaProjectProvider javaProjectProvider,
			ExportTemplateFactory exportTemplateFactory,
			String targetFile) {

		if (!canCreate(method)) {
			return null; 
		}

		return new SetupDialogExportOnline (
				parentShell, 
				method,
				javaProjectProvider,
				exportTemplateFactory,
				targetFile);
	}

	protected SetupDialogExportOnline(
			Shell parentShell, 
			MethodNode method,
			IJavaProjectProvider javaProjectProvider,
			ExportTemplateFactory exportTemplateFactory,
			String targetFile) {

		super(parentShell, 
				method, 
				false, 
				javaProjectProvider,
				exportTemplateFactory,
				targetFile);
	}

	@Override
	protected String getDialogTitle() {
		final String DIALOG_EXECUTE_ONLINE_TITLE = "Export online";
		return DIALOG_EXECUTE_ONLINE_TITLE;
	}

	@Override
	protected String getDialogMessage() {
		final String DIALOG_EXECUTE_ONLINE_MESSAGE = "Configure test data generation and export.";
		return DIALOG_EXECUTE_ONLINE_MESSAGE;
	}

	@Override
	protected int getContent() {
		return CONSTRAINTS_COMPOSITE | CHOICES_COMPOSITE
				| GENERATOR_SELECTION_COMPOSITE | TEST_CASES_EXPORT_COMPOSITE;
	}

}
