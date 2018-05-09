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

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SaveAsDialog;

public class EcSaveAsDialog {

	SaveAsDialog fDialog;

	public EcSaveAsDialog(Shell parent) {
		fDialog = new SaveAsDialog(parent);
	}

	public void setOriginalFile(Object file) {
		fDialog.setOriginalFile((IFile)file);
	}

	public void create() {
		fDialog.create();
	}

	public int open () {
		return fDialog.open();
	}

	public Object getResultPath() {
		return fDialog.getResult();
	}	
}