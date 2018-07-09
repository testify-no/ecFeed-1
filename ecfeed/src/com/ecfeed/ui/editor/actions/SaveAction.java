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

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.utils.IWorker;

public class SaveAction extends DescribedAction {

	private IWorker fSaveWorker;

	public SaveAction(IWorker saveWorker) {
		super(ActionId.SAVE);
		fSaveWorker = saveWorker;
	}

	@Override
	public void run() {

		fSaveWorker.doWork();
	}

	@Override
	public boolean isEnabled() {

		if (ApplicationContext.isApplicationTypeLocal()) {
			return true;
		}

		return false;
	}

}

