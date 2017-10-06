/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.data;

import com.ecfeed.core.model.RootNode;

public class TreeRootNodeWrapper {

	private final RootNode fModel;

	public TreeRootNodeWrapper(RootNode model) {
		fModel = model;
	}

	public RootNode getModel() {
		return fModel;
	}
}
