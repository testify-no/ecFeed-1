/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.editor.actions.ActionDescriptionProvider;
import com.ecfeed.ui.editor.actions.IActionProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.utils.SwtHelper;

public abstract class TreeViewerSection extends ViewerSection {

	public TreeViewerSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider,
			int style) {
		super(sectionContext, updateContext, javaProjectProvider, style);
	}

	@Override
	protected StructuredViewer createViewer(Composite viewerComposite, int style) {
		return createTreeViewer(viewerComposite, style);
	}

	@Override
	protected void createViewerColumns(){
	}

	protected TreeViewer createTreeViewer(Composite parent, int style) {
		Tree tree = new Tree(parent, style);
		tree.setLayoutData(viewerLayoutData());
		TreeViewer treeViewer = new TreeViewer(tree);
		return treeViewer;
	}

	protected Tree getTree(){
		return getTreeViewer().getTree();
	}

	protected TreeViewer getTreeViewer(){
		return (TreeViewer)getViewer();
	}

	@Override
	protected void setActionProvider(IActionProvider provider){
		super.setActionProvider(provider);
		if(provider.getAction(ActionDescriptionProvider.EXPAND_COLLAPSE.getId()) != null){
			createKeyListener(SwtHelper.getSpaceCode(), SWT.NONE, provider.getAction(ActionDescriptionProvider.EXPAND_COLLAPSE.getId()));
		}
	}
}
