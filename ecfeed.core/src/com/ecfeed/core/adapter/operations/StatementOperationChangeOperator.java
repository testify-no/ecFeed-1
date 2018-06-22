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
import com.ecfeed.core.model.EStatementOperator;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.StatementArray;

public class StatementOperationChangeOperator extends AbstractModelOperation {

	private StatementArray fTarget;
	private EStatementOperator fNewOperator;
	private EStatementOperator fCurrentOperator;

	public StatementOperationChangeOperator(StatementArray target, EStatementOperator operator) {
		super(OperationNames.CHANGE_STATEMENT_OPERATOR);
		fTarget = target;
		fNewOperator = operator;
		fCurrentOperator = target.getOperator();
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setOperator(fNewOperator);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new StatementOperationChangeOperator(fTarget, fCurrentOperator);
	}

}
