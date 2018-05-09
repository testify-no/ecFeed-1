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

import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ActionFactory {

	private static AboutAction fAboutAction = null;
	private static SaveAction fSaveAction = null;
	private static UndoAction fUndoAction = null;
	private static RedoAction fRedoAction = null;
	private static CopyAction fCopyAction = null;
	private static CopyToolbarAction fCopyToolbarAction = null;
	private static PasteToolbarAction fPasteToolbarAction = null;

	public static AboutAction getAboutAction() {

		if (fAboutAction == null) {
			fAboutAction = new AboutAction();
		}

		return fAboutAction;
	}

	public static SaveAction getSaveAction() {

		if (fSaveAction == null) {
			fSaveAction = new SaveAction();
		}

		return fSaveAction;
	}

	public static UndoAction getUndoAction() {

		if (fUndoAction == null) {
			fUndoAction = new UndoAction();
		}

		return fUndoAction;
	}	

	public static RedoAction getRedoAction() {

		if (fRedoAction == null) {
			fRedoAction = new RedoAction();
		}

		return fRedoAction;
	}	

	public static CopyAction getCopyAction() {

		if (fCopyAction == null) {
			fCopyAction = new CopyAction();
		}

		return fCopyAction;
	}


	public static CopyToolbarAction getCopyToolbarAction() {

		if (fCopyToolbarAction == null) {
			fCopyToolbarAction = new CopyToolbarAction();
		}

		return fCopyToolbarAction;
	}
	
	public static PasteToolbarAction getPasteToolbarAction() {

		if (fPasteToolbarAction == null) {
			fPasteToolbarAction = new PasteToolbarAction();
		}

		return fPasteToolbarAction;
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
