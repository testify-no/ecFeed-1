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

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.modelif.NodeSelectionUtils;

public abstract class ModelSelectionAction extends DescribedAction {

	private NodeSelectionUtils fSelectionUtils = null;

	public ModelSelectionAction(ActionId actionId, ISelectionProvider selectionProvider) {

		super(actionId);
		setSelectionProvider(selectionProvider);
	}

	public ModelSelectionAction(ActionId actionId) {

		this(actionId, null);
	}	

	protected void setSelectionProvider(ISelectionProvider selectionProvider) {
		fSelectionUtils = new NodeSelectionUtils(selectionProvider);
	}

	protected List<AbstractNode> getSelectedNodes(){

		if (fSelectionUtils == null) {
			return null;
		}

		return fSelectionUtils.getSelectedNodes();
	}

	protected boolean isSelectionSibling(){

		if (fSelectionUtils == null) {
			return false;
		}

		return fSelectionUtils.isSelectionSibling();
	}

	protected boolean isSelectionSingleType(){

		if (fSelectionUtils == null) {
			return false;
		}

		return fSelectionUtils.isSelectionSingleType();
	}

	protected NodeSelectionUtils getSelectionUtils(){

		return fSelectionUtils;
	}

}
