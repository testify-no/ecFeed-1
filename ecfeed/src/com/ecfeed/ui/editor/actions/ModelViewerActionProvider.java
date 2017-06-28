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
import com.ecfeed.ui.common.local.EclipseModelImplementer;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;


public class ModelViewerActionProvider extends ActionProvider {

	private static final String EDIT_GROUP = "edit";
	private static final String IMPLEMENT_GROUP = "implement";
	private static final String MOVE_GROUP = "move";
	private static final String VIEWER_GROUP = "viewer";
	private static final String INFO_GROUP = "info";

	
	BasicActionRunnerProvider fBasicActionRunnerProvider;

	public ModelViewerActionProvider(
			TreeViewer viewer, 
			IModelUpdateContext 
			context, 
			IFileInfoProvider fileInfoProvider,
			BasicActionRunnerProvider basicActionRunnerProvider,
			boolean selectRoot) {

		fBasicActionRunnerProvider = basicActionRunnerProvider;

		addEditActions(viewer, viewer, context, fileInfoProvider);

		if(fileInfoProvider != null && fileInfoProvider.isProjectAvailable()){
			addImplementationActions(viewer, context, fileInfoProvider);
		}

		addViewerActions(viewer, context, selectRoot);
		addMoveActions(viewer, context);
		addInfoActions();
	}

	public ModelViewerActionProvider(
			TableViewer viewer, 
			IModelUpdateContext context, 
			IFileInfoProvider fileInfoProvider) {

		addEditActions(viewer, viewer, context, fileInfoProvider);

		if(fileInfoProvider != null && fileInfoProvider.isProjectAvailable()){
			addImplementationActions(viewer, context, fileInfoProvider);
		}

		addViewerActions(viewer);
		addMoveActions(viewer, context);
	}

	private void addEditActions(
			ISelectionProvider selectionProvider,
			StructuredViewer structuredViewer,
			IModelUpdateContext context,
			IFileInfoProvider fileInfoProvider) {

		DeleteAction deleteAction = new DeleteAction(selectionProvider, context);
		addAction(EDIT_GROUP, new CopyAction(selectionProvider));
		addAction(EDIT_GROUP, new CutAction(new CopyAction(selectionProvider), deleteAction));
		addAction(EDIT_GROUP, new PasteAction(selectionProvider, context, fileInfoProvider));
		addAction(EDIT_GROUP, new InsertAction(selectionProvider, structuredViewer, context, fileInfoProvider));
		addAction(EDIT_GROUP, deleteAction);

		if (fBasicActionRunnerProvider != null) {
			addBasicEditActions();
		}
	}

	private void addBasicEditActions() {

		addAction(
				EDIT_GROUP, 
				new NamedActionWithRunner(
						GlobalActions.SAVE.getId(), GlobalActions.SAVE.getDescription(), 
						fBasicActionRunnerProvider.getSaveRunner()));

		addAction(
				EDIT_GROUP, 
				new NamedActionWithRunner(
						GlobalActions.UNDO.getId(), GlobalActions.UNDO.getDescription(), 
						fBasicActionRunnerProvider.getUndoRunner()));

		addAction(
				EDIT_GROUP, 
				new NamedActionWithRunner(
						GlobalActions.REDO.getId(), GlobalActions.REDO.getDescription(), 
						fBasicActionRunnerProvider.getRedoRunner()));
	}

	private void addImplementationActions(
			StructuredViewer viewer, IModelUpdateContext context, IFileInfoProvider fileInfoProvider) {
		
		IModelImplementer implementer = new EclipseModelImplementer(fileInfoProvider);
		addAction(IMPLEMENT_GROUP, new ImplementAction(viewer, context, implementer));
		addAction(IMPLEMENT_GROUP, new GoToImplementationAction(viewer, fileInfoProvider));
	}

	private void addMoveActions(ISelectionProvider selectionProvider, IModelUpdateContext context){
		
		addAction(MOVE_GROUP, new MoveUpDownAction(true, selectionProvider, context));
		addAction(MOVE_GROUP, new MoveUpDownAction(false, selectionProvider, context));
	}

	private void addViewerActions(TreeViewer viewer, IModelUpdateContext context, boolean selectRoot){
		
		addAction(VIEWER_GROUP, new SelectAllAction(viewer, selectRoot));
		addAction(VIEWER_GROUP, new ExpandCollapseAction(viewer));
	}

	private void addViewerActions(TableViewer viewer){
		
		addAction(VIEWER_GROUP, new SelectAllAction(viewer));
	}

	private void addInfoActions() {
		
		addAction(INFO_GROUP, new AboutAction());
		addAction(INFO_GROUP, new CheckForUpdatesAction());
	}

}
