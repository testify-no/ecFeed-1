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
import com.ecfeed.core.utils.MessageStack;
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

		String substituteType = getSubstituteType(actualLeftChoice);

		boolean isRandomizedChoice = 
				StatementConditionHelper.getChoiceRandomized(
						actualLeftChoice, 
						fParentRelationStatement.getLeftParameter());

		if(isRandomizedChoice) {
			return evaluateForRandomizedChoice(
					actualLeftChoice.getValueString(), 
					substituteType);
		}

		return evaluateForConstantChoice(actualLeftChoice, substituteType);
	}

	private String getSubstituteType(ChoiceNode leftChoice) {

		String typeName = leftChoice.getParameter().getType();
		return JavaTypeHelper.getSubstituteType(typeName);
	}

	private EvaluationResult evaluateForConstantChoice(
			ChoiceNode actualLeftChoice,
			String substituteType) {

		EStatementRelation relation = fParentRelationStatement.getRelation();

		if (relation == EStatementRelation.EQUAL || relation == EStatementRelation.NOT_EQUAL) {
			return evaluateEqualityIncludingParents(relation, actualLeftChoice);
		}

		String actualLeftValue = JavaTypeHelper.convertValueString(actualLeftChoice.getValueString(), substituteType);
		String rightValue = JavaTypeHelper.convertValueString(fRightChoice.getValueString(), substituteType);

		if (RelationMatcher.isMatchQuiet(relation, substituteType, actualLeftValue, rightValue)) {
			return EvaluationResult.TRUE;
		}

		return EvaluationResult.FALSE;		
	}

	private EvaluationResult evaluateForRandomizedChoice(
			String leftChoiceStr,
			String substituteType) {

		EStatementRelation relation = fParentRelationStatement.getRelation();
		String fRightValue = fRightChoice.getValueString();

		if (JavaTypeHelper.TYPE_NAME_STRING.equals(substituteType)) {
			return EvaluationResult.convertFromBoolean(leftChoiceStr.matches(fRightValue));
		}

		boolean result = 
				RangeValidator.isRightRangeInLeftRange(
						leftChoiceStr, fRightValue, relation, substituteType);

		return EvaluationResult.convertFromBoolean(result);
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
	public boolean isAmbiguous(
			List<List<ChoiceNode>> domain, 
			int parameterIndex, 
			EStatementRelation relation,
			MessageStack messageStack) {

		String substituteType = 
				JavaTypeHelper.getSubstituteType(
						fParentRelationStatement.getLeftParameter().getType(), 
						JavaTypeHelper.getStringTypeName());

		if (substituteType == null || parameterIndex >= domain.size()) {
			return false;
		}

		List<ChoiceNode> choices = domain.get(parameterIndex);		

		MethodParameterNode leftParameterNode = fParentRelationStatement.getLeftParameter();
		String leftChoiceStr = getChoiceString(choices, leftParameterNode);


		boolean isRandomizedChoice = StatementConditionHelper.getChoiceRandomized(choices,
				fParentRelationStatement.getLeftParameter());

		String rightValue = fRightChoice.getValueString();

		if (isRandomizedChoice) {
			if (isAmbiguousForRandomizedChoice(
					leftChoiceStr, rightValue, relation, substituteType)) {

				ChoiceNode leftChoiceNode = 
						StatementConditionHelper.getChoiceForMethodParameter(choices, leftParameterNode);

				ConditionHelper.addValuesMessageToStack(
						leftChoiceNode.toString(), relation, fRightChoice.toString(), messageStack);

				return true;
			}
			return false;
		}

		return false;
	}

	private static boolean isAmbiguousForRandomizedChoice(
			String leftChoiceStr,
			String rightValue,
			EStatementRelation relation,
			String substituteType) {

		return RangeValidator.isAmbiguous(
				leftChoiceStr, rightValue, relation, substituteType);
	}

	private static String getChoiceString(List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {

		ChoiceNode choiceNode = StatementConditionHelper.getChoiceForMethodParameter(choices, methodParameterNode);

		if (choiceNode == null) {
			return null;
		}

		return choiceNode.getValueString();
	}
}

