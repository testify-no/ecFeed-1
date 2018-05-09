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

import org.eclipse.jface.viewers.ISelectionProvider;

import com.ecfeed.ui.modelif.IModelUpdateContext;

public class MoveUpDownAction extends ModelModifyingAction {

	private boolean fUp;

	public MoveUpDownAction(boolean up, ISelectionProvider selectionProvider, IModelUpdateContext updateContext) {
		super(chooseId(up), selectionProvider, updateContext);
		fUp = up;
	}

	@Override
	public boolean isEnabled(){
		return getSelectionInterface().moveUpDownEnabed(fUp);
	}

	@Override 
	public void run(){
		getSelectionInterface().moveUpDown(fUp);
	}

	private static ActionId chooseId(boolean up){

		if (up) {
			return ActionId.MOVE_UP;
		}

		return ActionId.MOVE_DOWN;
	}

}
