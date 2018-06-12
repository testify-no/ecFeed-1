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

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.NodeClipboard;
import com.ecfeed.ui.modelif.NodeInterfaceFactory;

public class PasteAction extends ModelModifyingAction {

	IFileInfoProvider fFileInfoProvider;
	private TreeViewer fTreeViewer;
	private int fIndex;

	public PasteAction(
			ISelectionProvider selectionProvider, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		this(-1, selectionProvider, updateContext, fileInfoProvider);
	}

	public PasteAction(
			int index, 
			ISelectionProvider selectionProvider, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(GlobalActions.PASTE.getId(), 
				GlobalActions.PASTE.getDescription(), selectionProvider, updateContext);
		fIndex = index;
		fFileInfoProvider = fileInfoProvider;
	}

	public PasteAction(
			TreeViewer treeViewer, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		this(-1, treeViewer, updateContext, fileInfoProvider);
	}

	public PasteAction(
			int index, 
			TreeViewer treeViewer, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		this(index, (ISelectionProvider)treeViewer, updateContext, fileInfoProvider);
		fTreeViewer = treeViewer;
	}

	@Override
	public boolean isEnabled(){
		if(getSelectedNodes().size() != 1) return false;
		AbstractNodeInterface nodeIf = 
				NodeInterfaceFactory.getNodeInterface(
						getSelectedNodes().get(0), 
						getUpdateContext(), 
						fFileInfoProvider);
		if (fIndex != -1){
			return nodeIf.pasteEnabled(NodeClipboard.getContent(), fIndex);
		}
		return nodeIf.pasteEnabled(NodeClipboard.getContent());
	}

	@Override
	public void run(){
		AbstractNode parent = getSelectedNodes().get(0);
		AbstractNodeInterface parentIf = 
				NodeInterfaceFactory.getNodeInterface(parent, getUpdateContext(), fFileInfoProvider);

		Collection<AbstractNode> childrenToAdd = NodeClipboard.getContentCopy();
		String errorMessage = parentIf.canAddChildren(childrenToAdd);

		if (errorMessage != null) {
			ErrorDialog.open(Messages.CAN_NOT_PASTE_CHOICES, errorMessage);
			return;
		}

		parentIf.addChildren(childrenToAdd);
		if(fTreeViewer != null){
			fTreeViewer.expandToLevel(parent, 1);
		}
	}

}
