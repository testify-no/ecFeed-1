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

	public enum ActionType {
		MENU_AND_KEY_ACTION,
		KEY_ONLY_ACTION,
		TOOLBAR_ONLY_ACTION
	}

	private ActionType fActionType = ActionType.MENU_AND_KEY_ACTION;

	public DescribedAction(ActionId actionId) {

		ActionDescriptionProvider actionDescriptionProvider = ActionDescriptionProvider.getInstance();

		setId(actionDescriptionProvider.getStrId(actionId));

		setText(actionDescriptionProvider.getDescriptionWithShortcut(actionId));

		setImageDescriptor(actionDescriptionProvider.getImageDescriptor(actionId));
	}

	public void setActionType(ActionType actionType) {
		fActionType = actionType;
	}

	public ActionType getActionType() {
		return fActionType;
	}

	public boolean isKeyActionType() {

		if (fActionType == ActionType.MENU_AND_KEY_ACTION) {
			return true;
		}

		if (fActionType == ActionType.KEY_ONLY_ACTION) {
			return true;
		}

		return false;
	}

}
