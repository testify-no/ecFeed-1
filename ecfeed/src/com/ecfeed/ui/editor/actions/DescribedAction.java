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

import org.eclipse.jface.action.Action;

public class DescribedAction extends Action {

	private final String fDescriptionWithShortcut;
	private final String fId;

	public DescribedAction(ActionId actionId) {

		fId = ActionDescriptionProvider.getInstance().getStrId(actionId);
		fDescriptionWithShortcut = ActionDescriptionProvider.getInstance().getDescriptionWithShortcut(actionId);	
	}

	@Override
	public String getId() {
		return fId;
	}

	public String getDescriptionWithShortcut() {
		return fDescriptionWithShortcut;
	}
}
