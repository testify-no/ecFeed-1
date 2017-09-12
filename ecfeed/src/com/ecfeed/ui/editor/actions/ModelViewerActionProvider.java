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

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.IModelImplementer;
import com.ecfeed.ui.common.EclipseModelImplementer;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
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
			IModelUpdateContext context, 
			IJavaProjectProvider javaProjectProvider,
			BasicActionRunnerProvider basicActionRunnerProvider,
			boolean selectRoot) {

		fBasicActionRunnerProvider = basicActionRunnerProvider;

		addEditActions(viewer, viewer, context, javaProjectProvider);

		if (javaProjectProvider != null && ApplicationContext.isProjectAvailable()){
			addImplementationActions(viewer, context, javaProjectProvider);
		}

		addViewerActions(viewer, context, selectRoot);
		addMoveActions(viewer, context);
		addInfoActions();
	}

	public ModelViewerActionProvider(
			TableViewer viewer, 
			IModelUpdateContext context, 
			IJavaProjectProvider javaProjectProvider) {

		addEditActions(viewer, viewer, context, javaProjectProvider);

		if (javaProjectProvider != null && ApplicationContext.isProjectAvailable()) {
			addImplementationActions(viewer, context, javaProjectProvider);
		}

		addViewerActions(viewer);
		addMoveActions(viewer, context);
	}

	private void addEditActions(
			ISelectionProvider selectionProvider,
			StructuredViewer structuredViewer,
			IModelUpdateContext context,
			IJavaProjectProvider javaProjectProvider) {

		DeleteAction deleteAction = new DeleteAction(selectionProvider, context);
		addAction(EDIT_GROUP, new CopyAction(selectionProvider));
		addAction(EDIT_GROUP, new CutAction(new CopyAction(selectionProvider), deleteAction));
		addAction(EDIT_GROUP, new PasteAction(selectionProvider, context, javaProjectProvider));
		addAction(EDIT_GROUP, new InsertAction(selectionProvider, structuredViewer, context, javaProjectProvider));
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
			StructuredViewer viewer, IModelUpdateContext context, IJavaProjectProvider javaProjectProvider) {

		IModelImplementer implementer = new EclipseModelImplementer(javaProjectProvider);
		addAction(IMPLEMENT_GROUP, new ImplementAction(viewer, context, implementer));
		addAction(IMPLEMENT_GROUP, new GoToImplementationAction(viewer, javaProjectProvider));
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
