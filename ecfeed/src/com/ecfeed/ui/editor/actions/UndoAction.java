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

import org.eclipse.ui.operations.UndoActionHandler;

import com.ecfeed.application.ApplicationContext;


public class UndoAction extends NamedAction {

	public UndoAction(String id, String name) {
		super(id, name);
	}

	@Override
	public void run() {
		UndoActionHandler undoActionHandler = ApplicationContext.getUndoHandler();

		if (undoActionHandler != null) {
			undoActionHandler.run();
		}
	}

	@Override
	public boolean isEnabled(){
		return true;
	}
}
