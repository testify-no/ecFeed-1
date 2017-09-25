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
import org.eclipse.jface.resource.ImageDescriptor;

public class DescribedAction extends Action {

	public DescribedAction(ActionId actionId) {

		ActionDescriptionProvider actionDescriptionProvider = ActionDescriptionProvider.getInstance();
		
		setId(actionDescriptionProvider.getStrId(actionId));

		setText(actionDescriptionProvider.getDescriptionWithShortcut(actionId));

		setImageDescriptor(actionDescriptionProvider.getImageDescriptor(actionId));
	}

}
