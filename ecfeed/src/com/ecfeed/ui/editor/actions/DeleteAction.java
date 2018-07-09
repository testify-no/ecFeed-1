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

import java.util.Date;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.ecfeed.application.SessionDataStore;
import com.ecfeed.core.utils.SessionAttributes;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class DeleteAction extends ModelModifyingAction {

	private final long MIN_DIFFERENCE_BETWEEN_DELETE_ACTIONS_IN_MILLISECONDS = 400;

	public DeleteAction(ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(ActionId.DELETE, selectionProvider, updateContext);
	}

	@Override
	public boolean isEnabled() {
		return getSelectionInterface().deleteEnabled();
	}

	@Override
	public void run() {

		if (tooShortDimeBetweenActions_WorkaroundForDoubleDeleteKeyEvent()) {
			return;
		}

		getSelectionInterface().delete();
	}

	boolean tooShortDimeBetweenActions_WorkaroundForDoubleDeleteKeyEvent() {

		Date currentDateAndTime = new Date();

		Date lastDateAndTime = (Date)SessionDataStore.get(SessionAttributes.SA_DELETE_ACTION_START_TIME);

		SessionDataStore.set(SessionAttributes.SA_DELETE_ACTION_START_TIME, currentDateAndTime);

		if (lastDateAndTime == null) {
			return false;
		}

		long timeDifference = currentDateAndTime.getTime() - lastDateAndTime.getTime();

		if (timeDifference < MIN_DIFFERENCE_BETWEEN_DELETE_ACTIONS_IN_MILLISECONDS) {
			return true;
		}

		return false;
	}

}
