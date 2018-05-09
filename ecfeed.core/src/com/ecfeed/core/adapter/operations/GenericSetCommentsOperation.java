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
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ModelOperationException;

public class GenericSetCommentsOperation extends AbstractModelOperation {

	private String fComments;
	private AbstractNode fTarget;
	private String fCurrentComments;

	public GenericSetCommentsOperation(AbstractNode target, String comments) {

		super(OperationNames.SET_COMMENTS);
		fTarget = target;
		fComments = comments;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);
		fCurrentComments = fTarget.getDescription() != null ? fTarget.getDescription() : "";
		fTarget.setDescription(fComments);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new GenericSetCommentsOperation(fTarget, fCurrentComments);
	}

}
