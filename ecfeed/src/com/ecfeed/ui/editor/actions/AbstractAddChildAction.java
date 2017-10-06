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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractAddChildAction extends ModelModifyingAction {

	protected final static String ADD_TEST_SUITE_ACTION_NAME = "Generate test suite";

	protected final static String ADD_TEST_SUITE_ACTION_ID = "addTestCase";

	private StructuredViewer fViewer;

	public AbstractAddChildAction(ActionId actionId, StructuredViewer viewer, IModelUpdateContext updateContext) {
		super(actionId, viewer, updateContext);
		fViewer = viewer;
	}

	@Override
	public boolean isEnabled(){
		return getParentInterface() != null;
	}

	protected void select(AbstractNode node){
		if(fViewer != null && node != null){
			fViewer.setSelection(new StructuredSelection(node));
		}
	}

	protected abstract AbstractNodeInterface getParentInterface();
}
