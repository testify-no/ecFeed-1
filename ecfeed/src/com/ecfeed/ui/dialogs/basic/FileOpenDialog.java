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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import com.ecfeed.utils.EclipseHelper;

public class FileOpenDialog {

	public static String open(String[] filterExtensions) {
		FileDialog fFileDialog = new FileDialog(EclipseHelper.getActiveShell(), SWT.OPEN);
		fFileDialog.setText("Open");
		fFileDialog.setFilterPath(null);
		fFileDialog.setFilterExtensions(filterExtensions);
		return fFileDialog.open();
	}

	public static String open() {
		String[] filterExt = { "*; *.*" };
		return FileOpenDialog.open(filterExt);
	}
}
