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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;

public class SelectAllAction extends NamedAction {

	private TableViewer fTableViewer;
	private TreeViewer fTreeViewer;
	private boolean fSelectRoot;

	public SelectAllAction(TreeViewer viewer, boolean selectRoot){
		super(ActionDescriptionProvider.SELECT_ALL.getId(), ActionDescriptionProvider.SELECT_ALL.getDescription());
		fTreeViewer = viewer;
		fSelectRoot = selectRoot;
	}

	public SelectAllAction(TableViewer viewer){
		super(ActionDescriptionProvider.SELECT_ALL.getId(), ActionDescriptionProvider.SELECT_ALL.getDescription());
		fTableViewer = viewer;
	}

	@Override
	public boolean isEnabled(){
		return true;
	}

	@Override
	public void run(){
		if(fTreeViewer != null){
			fTreeViewer.expandAll();
			fTreeViewer.getTree().selectAll();
			if(fSelectRoot == false){
				fTreeViewer.getTree().deselect(fTreeViewer.getTree().getTopItem());
			}
		}
		if(fTableViewer != null){
			fTableViewer.getTable().selectAll();
		}
	}
}
