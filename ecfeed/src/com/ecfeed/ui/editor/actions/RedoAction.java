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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;

import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.editor.ModelEditorHelper;


public class RedoAction extends NamedAction {

	public RedoAction(String id, String name) {
		super(id, name);
	}

	@Override
	public void run() {
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
		IUndoContext undoContext = ModelEditorHelper.getActiveModelEditor().getUndoContext();

		try {
			operationHistory.redo(undoContext, null, null);
		} catch (ExecutionException e) {
			SystemLogger.logCatch("Can not redo operation.");
		}
	}

	@Override
	public boolean isEnabled(){
		return true;
	}
}
