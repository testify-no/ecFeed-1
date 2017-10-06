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

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class InsertAction extends ModelModifyingAction {

	IJavaProjectProvider fJavaProjectProvider;
	StructuredViewer fStructuredViewer;
	IModelUpdateContext fUpdateContext;

	public InsertAction(
			ISelectionProvider selectionProvider,
			StructuredViewer structuredViewer,
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider) {
		super(ActionId.INSERT, selectionProvider, updateContext);
		fJavaProjectProvider = javaProjectProvider;
		fStructuredViewer = structuredViewer;
		fUpdateContext = updateContext;
		
		setActionType(ActionType.KEY_ONLY_ACTION);
	}

	@Override
	public boolean isEnabled(){
		return true;
	}

	@Override 
	public void run(){
		List<AbstractNode> selectedNodes = getSelectedNodes();

		if (selectedNodes.size() != 1) {
			return;
		}

		AbstractNode abstractNode = selectedNodes.get(0);

		AbstractAddChildAction insertAction = 
				AddChildActionFactory.createMainInsertAction(
						abstractNode, fStructuredViewer, fUpdateContext, fJavaProjectProvider); 

		if (insertAction == null) {
			return;
		}

		insertAction.run();
	}

}
