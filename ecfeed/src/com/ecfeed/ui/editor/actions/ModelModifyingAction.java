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

import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.SelectionInterface;

public class ModelModifyingAction extends ModelSelectionAction {

	private IModelUpdateContext fUPdateContext = null;

	public ModelModifyingAction(ActionId actionId, ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(actionId, selectionProvider);
		setUpdateContext(updateContext);
	}

	public ModelModifyingAction(ActionId actionId) {
		super(actionId);
	}
	
	protected void setUpdateContext(IModelUpdateContext updateContext) {
		fUPdateContext = updateContext;
	}

	protected IModelUpdateContext getUpdateContext(){
		return fUPdateContext;
	}

	protected SelectionInterface getSelectionInterface(){
		return getSelectionUtils().getSelectionInterface(fUPdateContext);
	}
}
