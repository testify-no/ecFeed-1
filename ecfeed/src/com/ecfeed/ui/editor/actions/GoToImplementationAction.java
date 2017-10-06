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

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.NodeInterfaceFactory;

public class GoToImplementationAction extends ModelSelectionAction {

	private IJavaProjectProvider fJavaProjectProvider;

	public GoToImplementationAction(ISelectionProvider selectionProvider, IJavaProjectProvider javaProjectProvider) {
		super(ActionId.GO_TO_IMPLEMENTATION, selectionProvider);
		fJavaProjectProvider = javaProjectProvider;
	}

	@Override
	public void run() {

		if (getSelectedNodes().size() != 1) {
			return;
		}

		AbstractNode node = getSelectedNodes().get(0);
		AbstractNodeInterface nodeIf = 
				NodeInterfaceFactory.getNodeInterface(node, null, fJavaProjectProvider);

		nodeIf.goToImplementation();
	}

	@Override
	public boolean isEnabled() {

		if (getSelectedNodes().size() != 1) {
			return false;
		}

		AbstractNodeInterface nodeIf = 
				NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), null, fJavaProjectProvider);

		return nodeIf.goToImplementationEnabled();
	}

}
