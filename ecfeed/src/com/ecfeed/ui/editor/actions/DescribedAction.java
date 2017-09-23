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

public class DescribedAction extends NamedAction {

	public DescribedAction(ActionId actionId) {

		super(ActionDescriptionProvider2.getInstance().getStrId(actionId),
				ActionDescriptionProvider2.getInstance().getName(actionId));
	}

}
