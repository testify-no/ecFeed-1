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


public class BasicActionRunnerProvider {
	
	IActionRunner fSaveRunner;
	IActionRunner fUndoRunner;
	IActionRunner fRedoRunner;
	
	public BasicActionRunnerProvider(IActionRunner saveRunner, IActionRunner undoRunner, IActionRunner redoRunner) {
		fSaveRunner = saveRunner;
		fUndoRunner = undoRunner;
		fRedoRunner = redoRunner;
	}

	public IActionRunner getSaveRunner() {
		return fSaveRunner;
	}
	
	public IActionRunner getUndoRunner() {
		return fUndoRunner;
	}
	
	public IActionRunner getRedoRunner() {
		return fRedoRunner;
	}	

}
