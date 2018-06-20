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

import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.ObjectHelper;

public class ChoiceCondition implements IStatementCondition {

	private ChoiceNode fRightChoice;
	private RelationStatement fParentRelationStatement;

	public ChoiceCondition(ChoiceNode rightChoice, RelationStatement parentRelationStatement) {

		fRightChoice = rightChoice;
		fParentRelationStatement = parentRelationStatement;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> choices) {

		ChoiceNode choice = 
				StatementConditionHelper.getChoiceForMethodParameter(
						choices, fParentRelationStatement.getLeftParameter());

		if (choice == null) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		return evaluateChoice(choice);
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public ChoiceCondition getCopy() {
		return new ChoiceCondition(fRightChoice.makeClone(), fParentRelationStatement);
	}

	@Override
	public boolean updateReferences(MethodNode methodNode) {

		String parameterName = fParentRelationStatement.getLeftParameter().getName();
		MethodParameterNode methodParameterNode = methodNode.getMethodParameter(parameterName);

		String choiceName = fRightChoice.getQualifiedName();
		ChoiceNode choiceNode = methodParameterNode.getChoice(choiceName);

		if (choiceNode == null) {
			return false;
		}
		fRightChoice = choiceNode;

		return true;
	}

	@Override
	public Object getCondition(){
		return fRightChoice;
	}

	@Override
	public boolean compare(IStatementCondition condition) {

		if (condition instanceof ChoiceCondition == false) {
			return false;
		}

		ChoiceCondition compared = (ChoiceCondition)condition;

		return (fRightChoice.isMatch((ChoiceNode)compared.getCondition()));
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public String toString() {

		return StatementConditionHelper.createChoiceDescription(fRightChoice.getQualifiedName());
	}

	@Override
	public boolean mentions(MethodParameterNode methodParameterNode) {

		return false;
	}	

	public ChoiceNode getRightChoice() {
		return fRightChoice;
	}

	private EvaluationResult evaluateChoice(ChoiceNode actualLeftChoice) {

		String leftChoiceStr = actualLeftChoice.getValueString();
		String fRightValue = fRightChoice.getValueString();
		String typeName1 = actualLeftChoice.getParameter().getType();
		String substituteType = JavaTypeHelper.getSubstituteType(typeName1);
		EStatementRelation relation = fParentRelationStatement.getRelation();

		boolean isRandomizedChoice = 
				StatementConditionHelper.getChoiceRandomized(
						actualLeftChoice, fParentRelationStatement.getLeftParameter());

		if(isRandomizedChoice) {
			if(JavaTypeHelper.TYPE_NAME_STRING.equals(substituteType)) {
				return EvaluationResult.convertFromBoolean(leftChoiceStr.matches(fRightValue));
			}
			else {
				boolean result = StatementConditionHelper.isConstraintInChoiceRange(leftChoiceStr, fRightValue, relation, substituteType);
				return EvaluationResult.convertFromBoolean(result);
			}
		}

		if (relation == EStatementRelation.EQUAL || relation == EStatementRelation.NOT_EQUAL) {
			return evaluateEqualityIncludingParents(relation, actualLeftChoice);
		}

		String actualLeftValue = JavaTypeHelper.convertValueString(actualLeftChoice.getValueString(), substituteType);
		String rightValue = JavaTypeHelper.convertValueString(fRightChoice.getValueString(), substituteType);


		if (StatementConditionHelper.isRelationMatchQuiet(relation, substituteType, actualLeftValue, rightValue)) {
			return EvaluationResult.TRUE;
		}

		return EvaluationResult.FALSE;
	}

	private EvaluationResult evaluateEqualityIncludingParents(EStatementRelation relation, ChoiceNode choice) {

		boolean isMatch = false;

		if (choice == null || fRightChoice == null) {
			isMatch = ObjectHelper.isEqualWhenOneOrTwoNulls(choice, fRightChoice);
		} else {
			isMatch = choice.isMatchIncludingParents(fRightChoice);
		}

		switch (relation) {

		case EQUAL:
			return EvaluationResult.convertFromBoolean(isMatch);
		case NOT_EQUAL:
			return EvaluationResult.convertFromBoolean(!isMatch);
		default:
			ExceptionHelper.reportRuntimeException("Invalid relation.");
			return EvaluationResult.FALSE;
		}
	}

	@Override
	public boolean isAmbiguous(List<List<ChoiceNode>> domain, int parameterIndex, EStatementRelation relation) {

		String fRightValue = fRightChoice.getValueString();

		String substituteType = 
				JavaTypeHelper.getSubstituteType(
						fParentRelationStatement.getLeftParameter().getType(), JavaTypeHelper.getStringTypeName());

		if (substituteType == null || parameterIndex >= domain.size()) {
			return false;
		}

		List<ChoiceNode> choices = domain.get(parameterIndex);		

		String leftChoiceStr = getChoiceString(choices, fParentRelationStatement.getLeftParameter());

		if(relation.equals(EStatementRelation.EQUAL) || relation.equals(EStatementRelation.NOT_EQUAL)) {
			return false;
		}

		boolean isRandomizedChoice = StatementConditionHelper.getChoiceRandomized(choices,
				fParentRelationStatement.getLeftParameter());

		if (isRandomizedChoice) {
			if (JavaTypeHelper.TYPE_NAME_STRING.equals(substituteType)) {
				return leftChoiceStr.matches(fRightValue);
			} else {
				boolean result = StatementConditionHelper.isAmbiguous(leftChoiceStr,
						fRightValue, relation, substituteType);
				return result;
			}
		}

		if (StatementConditionHelper.isRelationMatchQuiet(relation, substituteType, leftChoiceStr, fRightValue)) {
			return true;
		}

		return false;
	}

	private static String getChoiceString(List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {

		ChoiceNode choiceNode = StatementConditionHelper.getChoiceForMethodParameter(choices, methodParameterNode);

		if (choiceNode == null) {
			return null;
		}

		return choiceNode.getValueString();
	}
}

