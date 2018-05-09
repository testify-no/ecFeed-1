/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.ConstraintOperationReplaceStatement;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;

public class ConstraintInterface extends AbstractNodeInterface {

	public ConstraintInterface(IModelUpdateContext updateContext, IJavaProjectProvider javaProjectProvider) {
		super(updateContext, javaProjectProvider);
	}

	@Override
	public ConstraintNode getOwnNode(){
		return (ConstraintNode)super.getOwnNode();
	}

	public boolean replaceStatement(AbstractStatement current, AbstractStatement newStatement) {
		if(current != newStatement){
			IModelOperation operation = new ConstraintOperationReplaceStatement(getOwnNode(), current, newStatement);
			return getOperationExecuter().execute(operation, Messages.DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean goToImplementationEnabled(){
		return false;
	}
}
