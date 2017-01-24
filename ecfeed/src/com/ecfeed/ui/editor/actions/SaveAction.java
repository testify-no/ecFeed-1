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


public class SaveAction extends NamedAction {

	IActionRunner fRunner;
	public SaveAction(String id, String name, IActionRunner runner) {
		super(id, name);
		fRunner = runner;
	}

	@Override
	public void run() {
		if (fRunner != null) {
			fRunner.run();
		}
	}

	@Override
	public boolean isEnabled(){
		return true;
	}
}
