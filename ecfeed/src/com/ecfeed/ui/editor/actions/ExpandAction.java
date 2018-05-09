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

import org.eclipse.jface.viewers.TreeViewer;

import com.ecfeed.core.model.AbstractNode;

public class ExpandAction extends ModelSelectionAction {

	protected TreeViewer fViewer;

	public ExpandAction(TreeViewer viewer) {
		super(ActionId.EXPAND, viewer);
		fViewer = viewer;
	}

	@Override
	public boolean isEnabled(){
		boolean enabled = false;
		List<AbstractNode> nodes = getSelectedNodes();
		for(AbstractNode node : nodes){
			if((fViewer.isExpandable(node)) && (getBranchExpandedState(node, fViewer) == false)){
				enabled = true;
			}
		}
		return enabled;
	}

	@Override
	public void run(){
		for(AbstractNode node : getSelectedNodes()){
			fViewer.expandToLevel(node, TreeViewer.ALL_LEVELS);
		}
	}

	protected boolean getBranchExpandedState(AbstractNode branchRoot, TreeViewer viewer){
		if(branchRoot.getChildren().size() == 0) return true;
		if(viewer.getExpandedState(branchRoot) == false) return false;
		for(AbstractNode child : branchRoot.getChildren()){
			if(getBranchExpandedState(child, viewer) == false) return false;
		}
		return true;
	}
}
