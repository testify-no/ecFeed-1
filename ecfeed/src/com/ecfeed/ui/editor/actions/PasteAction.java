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

public class PasteAction extends ModelModifyingAction {

	private IJavaProjectProvider fJavaProjectProvider;

	public PasteAction(
			ISelectionProvider selectionProvider, 
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider) {
		super(ActionId.PASTE, selectionProvider, updateContext);
		fJavaProjectProvider = javaProjectProvider;
	}

	@Override
	public boolean isEnabled(){

		List<AbstractNode> selectedNodes = getSelectedNodes();

		if (selectedNodes == null) {
			return false;
		}

		if (getSelectedNodes().size() != 1) {
			return false;
		}

		AbstractNodeInterface nodeIf = 
				NodeInterfaceFactory.getNodeInterface(
						getSelectedNodes().get(0), 
						getUpdateContext(), 
						fJavaProjectProvider);

		return nodeIf.pasteEnabled(NodeClipboard.getContent());
	}

	@Override
	public void run() {
		AbstractNode parent = getSelectedNodes().get(0);
		AbstractNodeInterface parentIf = 
				NodeInterfaceFactory.getNodeInterface(parent, getUpdateContext(), fJavaProjectProvider);

		Collection<AbstractNode> childrenToAdd = NodeClipboard.getContentCopy();
		String errorMessage = parentIf.canAddChildren(childrenToAdd);

		if (errorMessage != null) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.CAN_NOT_PASTE_CHOICES, errorMessage);
			return;
		}

		parentIf.addChildren(childrenToAdd);
	}

}
