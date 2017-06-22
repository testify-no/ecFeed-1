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

import org.eclipse.jface.viewers.TreeViewer;

import com.ecfeed.core.model.AbstractNode;

public class ExpandOneLevelAction extends ExpandAction {

	public ExpandOneLevelAction(TreeViewer viewer) {
		super(viewer);
	}

	@Override
	public void run() { 

		for (AbstractNode node : getSelectedNodes()) {
			fViewer.expandToLevel(node, 1);
		}
	}

}
