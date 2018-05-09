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
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ModelOperationException;

public class ConstraintOperationReplaceStatement extends AbstractModelOperation{

	private AbstractStatement fNewStatement;
	private AbstractStatement fCurrentStatement;
	private ConstraintNode fTarget;

	public ConstraintOperationReplaceStatement(ConstraintNode target, AbstractStatement current, AbstractStatement newStatement) {
		super(OperationNames.REPLACE_STATEMENT);
		fTarget = target;
		fCurrentStatement = current;
		fNewStatement = newStatement;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);
		Constraint constraint = fTarget.getConstraint();

		if (constraint.getPremise() == fCurrentStatement) {
			constraint.setPremise(fNewStatement);
		}
		else if (constraint.getConsequence() == fCurrentStatement) {
			constraint.setConsequence(fNewStatement);
		}
		else {
			ModelOperationException.report(Messages.TARGET_STATEMENT_NOT_FOUND_PROBLEM);
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ConstraintOperationReplaceStatement(fTarget, fNewStatement, fCurrentStatement);
	}

}
