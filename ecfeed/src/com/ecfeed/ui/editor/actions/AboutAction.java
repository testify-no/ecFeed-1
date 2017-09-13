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

import com.ecfeed.ui.dialogs.AboutDialog;

public class AboutAction extends NamedAction {

	public AboutAction(){
		super(GlobalActions.ABOUT.getId(), GlobalActions.ABOUT.getDescription());
	}

	@Override
	public void run() {
		AboutDialog.open();
	}

	@Override
	public boolean isEnabled(){
		return true;
	}
}
