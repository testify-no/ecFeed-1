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
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;

public class MethodOperationRemoveConstraint extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private ConstraintNode fConstraint;
	private int fIndex;

	public MethodOperationRemoveConstraint(MethodNode target, ConstraintNode constraint){
		super(OperationNames.REMOVE_CONSTRAINT);
		fMethodNode = target;
		fConstraint = constraint;
		fIndex = fConstraint.getIndex();
	}

	@Override
	public void execute() throws ModelOperationException {

		setNodeToBeSelectedAfterTheOperation(fMethodNode);
		fIndex = fConstraint.getIndex();
		fMethodNode.removeConstraint(fConstraint);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationAddConstraint(fMethodNode, fConstraint, fIndex);
	}

}
