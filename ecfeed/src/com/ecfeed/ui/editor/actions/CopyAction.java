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

import java.util.List;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.modelif.NodeClipboard;

public class CopyAction extends ModelSelectionAction {

	public CopyAction(){
		super(ActionId.COPY);
	}

	@Override
	public void run() {
		NodeClipboard.setContent(getSelectedNodes());
	}

	@Override
	public boolean isEnabled() {

		List<AbstractNode> selectedNodes = getSelectedNodes();

		if (selectedNodes == null) {
			return false;
		}

		return selectedNodes.size() > 0 && isSelectionSingleType();
	}
}
