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
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;

import com.ecfeed.core.adapter.IModelImplementer;
import com.ecfeed.ui.common.EclipseModelImplementer;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;


public class ModelViewerActionProvider extends ActionGroups {

	public ModelViewerActionProvider(TreeViewer viewer, IModelUpdateContext context, boolean selectRoot) {
		this(viewer, context, null, selectRoot);
	}

	public ModelViewerActionProvider(
			TreeViewer viewer, 
			IModelUpdateContext 
			context, 
			IFileInfoProvider fileInfoProvider, 
			boolean selectRoot) {
		addEditActions(viewer, viewer, context, fileInfoProvider);
		if(fileInfoProvider != null && fileInfoProvider.isProjectAvailable()){
			addImplementationActions(viewer, context, fileInfoProvider);
		}

		boolean isNameWithShortcut = fileInfoProvider.isProjectAvailable();

		addViewerActions(viewer, context, selectRoot, isNameWithShortcut);
		addMoveActions(viewer, context, isNameWithShortcut);
	}

	public ModelViewerActionProvider(
			TableViewer viewer, 
			IModelUpdateContext context, 
			IFileInfoProvider fileInfoProvider) {

		addEditActions(viewer, viewer, context, fileInfoProvider);

		if(fileInfoProvider != null && fileInfoProvider.isProjectAvailable()){
			addImplementationActions(viewer, context, fileInfoProvider);
		}

		boolean isNameWithShortcut = fileInfoProvider.isProjectAvailable();

		addViewerActions(viewer, isNameWithShortcut);
		addMoveActions(viewer, context, isNameWithShortcut);
	}

	private void addEditActions(
			ISelectionProvider selectionProvider,
			StructuredViewer structuredViewer,
			IModelUpdateContext context,
			IFileInfoProvider fileInfoProvider) {

		boolean isNameWithShortcut = fileInfoProvider.isProjectAvailable();

		DeleteAction deleteAction = new DeleteAction(selectionProvider, context, isNameWithShortcut);
		addAction("edit", new CopyAction(selectionProvider, isNameWithShortcut));
		addAction("edit", new CutAction(new CopyAction(selectionProvider, isNameWithShortcut), deleteAction, isNameWithShortcut));
		addAction("edit", new PasteAction(selectionProvider, context, fileInfoProvider));
		addAction("edit", new InsertAction(selectionProvider, structuredViewer, context, fileInfoProvider, isNameWithShortcut));
		addAction("edit", deleteAction);
	}

	private void addImplementationActions(StructuredViewer viewer, IModelUpdateContext context, IFileInfoProvider fileInfoProvider) {
		IModelImplementer implementer = new EclipseModelImplementer(fileInfoProvider);
		addAction("implement", new ImplementAction(viewer, context, implementer));
		addAction("implement", new GoToImplementationAction(viewer, fileInfoProvider));
	}

	private void addMoveActions(ISelectionProvider selectionProvider, IModelUpdateContext context, boolean isNameWithShortcut){
		addAction("move", new MoveUpDownAction(true, selectionProvider, context, isNameWithShortcut));
		addAction("move", new MoveUpDownAction(false, selectionProvider, context, isNameWithShortcut));
	}

	private void addViewerActions(TreeViewer viewer, IModelUpdateContext context, boolean selectRoot, boolean isNameWithShortcut){
		addAction("viewer", new SelectAllAction(viewer, selectRoot, isNameWithShortcut));
		addAction("viewer", new ExpandCollapseAction(viewer, isNameWithShortcut));
	}

	private void addViewerActions(TableViewer viewer, boolean isNameWithShortcut){
		addAction("viewer", new SelectAllAction(viewer, isNameWithShortcut));
	}

}
