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

public abstract class SetupDialogOnline extends GeneratorSetupDialog {

	protected SetupDialogOnline(
			Shell parentShell, 
			MethodNode method, 
			boolean generateExecutables,
			IJavaProjectProvider javaProjectProvider,
			ExportTemplateFactory exportTemplateFactory,
			String targetFile) {

		super(parentShell, 
				method, 
				generateExecutables, 
				javaProjectProvider,
				exportTemplateFactory,
				targetFile);
	}
}
