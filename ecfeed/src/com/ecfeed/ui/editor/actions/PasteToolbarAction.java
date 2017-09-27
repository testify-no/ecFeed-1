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

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.NodeClipboard;
import com.ecfeed.ui.modelif.NodeInterfaceFactory;

public class PasteToolbarAction extends PasteAction {

	private IJavaProjectProvider fJavaProjectProvider;

	public PasteToolbarAction() {
		super();
	}

	public void setContext(
			ISelectionProvider selectionProvider, 
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider) {

		setSelectionProvider(selectionProvider);
		setUpdateContext(updateContext);
		fJavaProjectProvider = javaProjectProvider;
	}

	@Override
	public boolean isEnabled(){

		return true;
	}

}
