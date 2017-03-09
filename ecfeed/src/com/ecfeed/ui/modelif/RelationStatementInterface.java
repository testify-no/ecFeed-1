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
import com.ecfeed.core.model.ChoiceCondition;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.IStatementCondition;
import com.ecfeed.core.model.LabelCondition;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ParameterCondition;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.StatementConditionHelper;
import com.ecfeed.core.model.ValueCondition;
import com.ecfeed.ui.common.Messages;

public class RelationStatementInterface extends AbstractStatementInterface{

	EStatementRelation fRelation;

	public RelationStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	@Override
	public boolean setRelation(EStatementRelation relation) {

		fRelation = relation;

		if (relation != getOwnStatement().getRelation()) {
			IModelOperation operation = new StatementOperationSetRelation(getOwnStatement(), relation);
			return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean setNewCondition(String text) {

		String conditionName = getOwnStatement().getConditionName();

		if (conditionName.equals(text)) {
			return false;
		}

		MethodParameterNode leftParameter = getOwnStatement().getLeftParameter();

		IStatementCondition newCondition = createNewCondition(text, leftParameter);
		IModelOperation operation = new StatementOperationSetCondition(getOwnStatement(), newCondition);

		return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
	}

	private IStatementCondition createNewCondition(String text, MethodParameterNode parameter) {

		if (StatementConditionHelper.containsChoiceTypeInfo(text)) {

			String choiceName = StatementConditionHelper.removeChoiceTypeInfo(text);
			return new ChoiceCondition(parameter.getChoice(choiceName), getOwnStatement());
		}

		if (StatementConditionHelper.containsLabelTypeInfo(text)) {

			return new LabelCondition(StatementConditionHelper.removeLabelTypeInfo(text), getOwnStatement());
		}

		if (StatementConditionHelper.containsParameterTypeInfo(text)) {

			String parameterName = StatementConditionHelper.removeParameterTypeInfo(text);
			MethodNode methodNode = parameter.getMethod();
			MethodParameterNode rightParameter = (MethodParameterNode)methodNode.getParameter(parameterName);

			return new ParameterCondition(rightParameter, getOwnStatement());
		}

		if (StatementConditionHelper.containsNoTypeInfo(text)) {

			return new ValueCondition(text, getOwnStatement());
		}

		return null;
	}

	@Override
	public String getConditionValue() {
		return getOwnStatement().getConditionName();
	}

	@Override
	public RelationStatement getOwnStatement() {
		return (RelationStatement)super.getOwnStatement();
	}
}
