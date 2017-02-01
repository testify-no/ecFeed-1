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

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.EStatementOperator;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.StaticStatement;

public class AbstractStatementInterface extends OperationExecuter {

	AbstractStatement fAbstractStatement;

	public AbstractStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(AbstractStatement target){
		fAbstractStatement = target;
	}

	protected AbstractStatement getStatement(){
		return fAbstractStatement;
	}

	public boolean remove(){
		if(fAbstractStatement.getParent() != null){
			return getParentInterface().removeChild(fAbstractStatement);
		}
		return false;
	}

	public boolean removeChild(AbstractStatement child){
		return false;
	}


	public AbstractStatement addNewStatement(){
		AbstractStatement statement = new StaticStatement(true);
		if(addStatement(statement)){
			return statement;
		}
		return null;
	}

	public boolean addStatement(AbstractStatement statement){
		if(fAbstractStatement.getParent() != null){
			return getParentInterface().addStatement(statement);
		}
		return false;
	}

	public AbstractStatementInterface getParentInterface(){
		AbstractStatement parent = fAbstractStatement.getParent();
		if(parent != null){
			return StatementInterfaceFactory.getInterface(parent, getUpdateContext());
		}
		return null;
	}

	public boolean setRelation(EStatementRelation relation) {
		return false;
	}

	public boolean setConditionValue(String text) {
		return false;
	}

	public String getConditionValue() {
		return null;
	}

	public boolean setOperator(EStatementOperator operator) {
		return false;
	}

	public EStatementOperator getOperator() {
		return null;
	}

	public boolean replaceChild(AbstractStatement child, AbstractStatement newStatement) {
		return false;
	}
}
