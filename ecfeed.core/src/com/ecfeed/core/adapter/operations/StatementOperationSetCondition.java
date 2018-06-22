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
import com.ecfeed.core.model.IStatementCondition;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RelationStatement;

public class StatementOperationSetCondition extends AbstractModelOperation {

	private RelationStatement fTarget;
	private IStatementCondition fCurrentCondition;
	private IStatementCondition fNewCondition;

	public StatementOperationSetCondition(RelationStatement target, IStatementCondition condition) {
		super(OperationNames.SET_STATEMENT_CONDITION);
		fTarget = target;
		fNewCondition = condition;
		fCurrentCondition = target.getCondition();
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setCondition(fNewCondition);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new StatementOperationSetCondition(fTarget, fCurrentCondition);
	}

}
