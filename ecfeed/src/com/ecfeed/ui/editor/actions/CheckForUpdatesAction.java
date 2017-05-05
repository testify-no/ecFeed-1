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

import com.ecfeed.ui.dialogs.CheckForUpdatesDialog;

public class CheckForUpdatesAction extends NamedAction {

	public CheckForUpdatesAction(){
		super(GlobalActions.CHECK_FOR_UPDATES.getId(), GlobalActions.CHECK_FOR_UPDATES.getDescription());
	}

	@Override
	public void run() {
		CheckForUpdatesDialog.openUnconditionally();
	}

	@Override
	public boolean isEnabled(){
		return true;
	}
}
