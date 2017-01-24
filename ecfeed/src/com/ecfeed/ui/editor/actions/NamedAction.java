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

public class NamedAction extends Action {

	private final String fName;
	private final String fId;

	public NamedAction(String id, String name){
		fId = id;
		fName = name;
	}

	public String getName(){
		return fName;
	}

	@Override
	public String getId(){
		return fId;
	}
}
