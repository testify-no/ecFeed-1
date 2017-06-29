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

public class SetupDialogGenerateTestSuite extends GeneratorSetupDialog {

	public SetupDialogGenerateTestSuite(
			Shell parentShell, 
			MethodNode method,
			ExportTemplateFactory exportTemplateFactory,
			IJavaProjectProvider javaProjectProvider) {

		super(parentShell, 
				method, 
				false, 
				javaProjectProvider,
				exportTemplateFactory,
				null);
	}

	@Override
	protected String getDialogTitle() {
		final String DIALOG_GENERATE_TEST_SUITE_TITLE = "Generate test suite";
		return DIALOG_GENERATE_TEST_SUITE_TITLE;
	}

	@Override
	protected int getContent() {
		return CONSTRAINTS_COMPOSITE | CHOICES_COMPOSITE
				| TEST_SUITE_NAME_COMPOSITE | GENERATOR_SELECTION_COMPOSITE;
	}
}
