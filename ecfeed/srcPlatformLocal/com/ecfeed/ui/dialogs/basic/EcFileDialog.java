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

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class EcFileDialog {

	FileDialog fDialog;
	
	public EcFileDialog(Shell parent, int style) {
		fDialog = new FileDialog(parent, style);
	}
	
	public void setFilterExtensions (String [] extensions) {
		fDialog.setFilterExtensions(extensions);
	}

	public String open () {
		return fDialog.open();
	}
}