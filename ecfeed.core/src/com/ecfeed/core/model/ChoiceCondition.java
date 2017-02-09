/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaTypeHelper;

public class ChoiceCondition implements IStatementCondition {

	private ChoiceNode fChoice;
	private MethodParameterNode fMethodParameterNode;
	private EStatementRelation fRelation;

	public ChoiceCondition(ChoiceNode choice, MethodParameterNode parameter, EStatementRelation relation) {
		fChoice = choice;
		fMethodParameterNode = parameter;
		fRelation = relation;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> choices) {

		ChoiceNode choice = StatementConditionHelper.getChoiceForMethodParameter(choices, fMethodParameterNode);

		if (choice == null) {
			return false;
		}

		return evaluateChoice(choice);
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public ChoiceCondition getCopy() {
		return new ChoiceCondition(fChoice.makeClone(), fMethodParameterNode, fRelation);
	}

	@Override
	public boolean updateReferences(MethodParameterNode parameter) {

		ChoiceNode condition = parameter.getChoice(fChoice.getQualifiedName());

		if (condition == null) {
			return false;
		}

		fChoice = condition;
		return true;
	}

	@Override
	public Object getCondition(){
		return fChoice;
	}

	@Override
	public boolean compare(IStatementCondition condition) {

		if (condition instanceof ChoiceCondition == false) {
			return false;
		}

		ChoiceCondition compared = (ChoiceCondition)condition;

		return (fChoice.isMatch((ChoiceNode)compared.getCondition()));
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return fChoice.getQualifiedName();
	}

	public ChoiceNode getChoice() {
		return fChoice;
	}

	private boolean evaluateChoice(ChoiceNode actualChoice) {

		if (fRelation == EStatementRelation.EQUAL || fRelation == EStatementRelation.NOT_EQUAL) {
			return evaluateEqualityIncludingParents(fRelation, actualChoice);
		}

		String typeName = actualChoice.getParameter().getType();

		String actualValue = JavaTypeHelper.convertValueString(actualChoice.getValueString(), typeName);
		String valueToMatch = JavaTypeHelper.convertValueString(fChoice.getValueString(), typeName);

		return StatementConditionHelper.isRelationMatchQuiet(fRelation, typeName, actualValue, valueToMatch);
	}

	private boolean evaluateEqualityIncludingParents(EStatementRelation relation, ChoiceNode choice) {

		boolean isMatch = choice.isMatchIncludingParents(fChoice);

		switch (relation) {

		case EQUAL:
			return isMatch;
		case NOT_EQUAL:
			return !isMatch;
		default:
			ExceptionHelper.reportRuntimeException("Invalid relation.");
			return false;
		}
	}

}

