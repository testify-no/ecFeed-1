/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import com.ecfeed.ui.editor.actions.ActionDescriptionProvider;
import com.ecfeed.ui.editor.actions.NamedAction;

public class SaveAction extends NamedAction {

	public SaveAction() {
		super(ActionDescriptionProvider.SAVE.getId(), ActionDescriptionProvider.SAVE.getDescription());
	}

	@Override
	public void run() {
		ModelEditorHelper.saveActiveEditor();
	}

}

