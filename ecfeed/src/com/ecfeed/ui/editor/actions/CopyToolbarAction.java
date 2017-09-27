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

import com.ecfeed.ui.modelif.NodeClipboard;

public class CopyToolbarAction extends ModelSelectionAction {

	public CopyToolbarAction(ISelectionProvider selectionProvider){
		super(ActionId.COPY, selectionProvider);
		setActionType(ActionType.TOOLBAR_ONLY_ACTION);
	}

	public CopyToolbarAction(){
		super(ActionId.COPY);
	}

	@Override
	public void run() {
		NodeClipboard.setContent(getSelectedNodes());
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
