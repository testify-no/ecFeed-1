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

public class BasicActionProvider {

	NamedAction fSaveAction;
	NamedAction fUndoAction;
	NamedAction fRedoAction;

	public BasicActionProvider(NamedAction saveAction, NamedAction undoAction, NamedAction redoAction) {
		fSaveAction = saveAction;
		fUndoAction = undoAction;
		fRedoAction = redoAction;
	}

	public NamedAction getSaveRunner() {
		return fSaveAction;
	}

	public NamedAction getUndoRunner() {
		return fUndoAction;
	}

	public NamedAction getRedoRunner() {
		return fRedoAction;
	}	

}
