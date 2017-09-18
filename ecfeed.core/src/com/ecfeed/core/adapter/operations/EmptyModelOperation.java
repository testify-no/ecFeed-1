/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.model.ModelOperationException;

public class EmptyModelOperation extends AbstractModelOperation {

	public EmptyModelOperation() {
		super("");
	}

	@Override
	public void execute() throws ModelOperationException {
	}

	@Override
	public IModelOperation reverseOperation() {
		return null;
	}
	
}
