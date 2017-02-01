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
import com.ecfeed.core.adapter.operations.StatementOperationSetCondition;
import com.ecfeed.core.adapter.operations.StatementOperationSetRelation;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ChoicesParentStatement.ICondition;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.ui.common.Messages;

public class ChoicesParentStatementInterface extends AbstractStatementInterface{

	public ChoicesParentStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	@Override
	public boolean setRelation(EStatementRelation relation) {
		if (relation != getStatement().getRelation()) {
			IModelOperation operation = new StatementOperationSetRelation(getStatement(), relation);
			return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean setConditionValue(String text) {

		String conditionName = getStatement().getConditionName();
		
		if (conditionName.equals(text)) {
			return false;
		}
		
		MethodParameterNode leftParameter = getStatement().getParameter();
		
		ICondition newCondition = createNewCondition(text, leftParameter);
		IModelOperation operation = new StatementOperationSetCondition(getStatement(), newCondition);
		
		return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
	}
	
	private ICondition createNewCondition(String string, MethodParameterNode parameter) {
		
		if (!containsTypeInfo(string, null)) {
			return getStatement().new ChoiceCondition(parameter.getChoice(string));
		}
		
		if (containsTypeInfo(string, "label")) {
			return getStatement().new LabelCondition(removeTypeInfo(string, "label"));
		}
		
		if (containsTypeInfo(string, "parameter")) {
			String parameterName = removeTypeInfo(string, "parameter"); 
			MethodNode methodNode = parameter.getMethod();
			MethodParameterNode rightParameter = (MethodParameterNode)methodNode.getParameter(parameterName);
					
			return getStatement().new ParameterCondition(rightParameter);
		}
		
		return null;
		
		
	}
	
	private static boolean containsTypeInfo(String string, String typeDescription) {
		
		if (!(string.contains("["))) {
			return false;
		}
		
		if (!(string.contains("]"))) {
			return false;
		}		
		
		if (typeDescription != null) {
			if (!string.contains("[" + typeDescription + "]")) {
				return false;
			}
		}
		
		return true;
	}
	
	private String removeTypeInfo(String string, String typeDescription) {
		return StringHelper.removeFromPostfix("[" + typeDescription + "]", string);
	}
	

	@Override
	public String getConditionValue() {
		return getStatement().getConditionName();
	}

	@Override
	protected ChoicesParentStatement getStatement() {
		return (ChoicesParentStatement)super.getStatement();
	}
}
