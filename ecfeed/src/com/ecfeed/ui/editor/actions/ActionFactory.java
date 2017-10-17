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

import com.ecfeed.application.SessionDataStore;
import com.ecfeed.core.utils.SessionAttributes;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ActionFactory {

	private AboutAction fAboutAction = null;
	private SaveAction fSaveAction = null;
	private UndoAction fUndoAction = null;
	private RedoAction fRedoAction = null;
	private CopyAction fCopyAction = null;
	private CopyToolbarAction fCopyToolbarAction = null;
	private PasteToolbarAction fPasteToolbarAction = null;

	private static ActionFactory getSessionInstance() {

		ActionFactory actionFactory = (ActionFactory)SessionDataStore.get(
				SessionAttributes.SA_ACTION_FACTORY);

		if (actionFactory == null) {
			actionFactory = new ActionFactory();
			SessionDataStore.set(SessionAttributes.SA_ACTION_FACTORY, actionFactory);
		}

		return actionFactory;
	}

	public static AboutAction getAboutAction() {

		ActionFactory actionFactory = getSessionInstance();

		if (actionFactory.fAboutAction == null) {
			actionFactory.fAboutAction = new AboutAction();
		}

		return actionFactory.fAboutAction;
	}

	public static SaveAction getSaveAction() {

		ActionFactory actionFactory = getSessionInstance();

		if (actionFactory.fSaveAction == null) {
			actionFactory.fSaveAction = new SaveAction();
		}

		return actionFactory.fSaveAction;
	}

	public static UndoAction getUndoAction() {

		ActionFactory actionFactory = getSessionInstance();

		if (actionFactory.fUndoAction == null) {
			actionFactory.fUndoAction = new UndoAction();
		}

		return actionFactory.fUndoAction;
	}	

	public static RedoAction getRedoAction() {

		ActionFactory actionFactory = getSessionInstance();

		if (actionFactory.fRedoAction == null) {
			actionFactory.fRedoAction = new RedoAction();
		}

		return actionFactory.fRedoAction;
	}	

	public static CopyAction getCopyAction() {

		ActionFactory actionFactory = getSessionInstance();

		if (actionFactory.fCopyAction == null) {
			actionFactory.fCopyAction = new CopyAction();
		}

		return actionFactory.fCopyAction;
	}

	public static CopyToolbarAction getCopyToolbarAction() {

		ActionFactory actionFactory = getSessionInstance();

		if (actionFactory.fCopyToolbarAction == null) {
			actionFactory.fCopyToolbarAction = new CopyToolbarAction();
		}

		return actionFactory.fCopyToolbarAction;
	}

	public static PasteToolbarAction getPasteToolbarAction() {

		ActionFactory actionFactory = getSessionInstance();

		if (actionFactory.fPasteToolbarAction == null) {
			actionFactory.fPasteToolbarAction = new PasteToolbarAction();
		}

		return actionFactory.fPasteToolbarAction;
	}

	public static void setContextForActions(
			ISelectionProvider selectionProvider,
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider) {

		getCopyAction().setContext(selectionProvider);
		getCopyToolbarAction().setContext(selectionProvider);
		getPasteToolbarAction().setContext(selectionProvider, updateContext, javaProjectProvider);
	}

}
