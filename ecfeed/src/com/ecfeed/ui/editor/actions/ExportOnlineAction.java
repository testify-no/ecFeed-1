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

import org.eclipse.jface.viewers.ISelectionProvider;

import com.ecfeed.core.utils.EcException;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.modelif.MethodInterface;

public class ExportOnlineAction extends ModelSelectionAction {

	private IJavaProjectProvider fJavaProjectProvider;
	private MethodInterface fMethodInterface;

	public ExportOnlineAction(
			IJavaProjectProvider javaProjectProvider, 
			ISelectionProvider selectionProvider, 
			MethodInterface methodInterface) {

		super(ActionId.EXPORT_ONLINE, selectionProvider);
		fJavaProjectProvider = javaProjectProvider;
		fMethodInterface = methodInterface;
	}

	@Override
	public void run() {
		try {
			fMethodInterface.executeOnlineExport(fJavaProjectProvider);
		} catch (EcException e) {
			final String MSG = "Can not execute online export.";
			ExceptionCatchDialog.open(MSG, e.getMessage());
		}
	}

	@Override
	public boolean isEnabled(){
		return true;
	}

}
