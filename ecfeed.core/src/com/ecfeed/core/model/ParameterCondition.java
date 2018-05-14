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
import com.ecfeed.core.utils.JavaTypeHelper;


public class ParameterCondition implements IStatementCondition {

	//cartesian
	private MethodParameterNode fRightParameterNode;
	private RelationStatement fParentRelationStatement;

	public ParameterCondition(MethodParameterNode rightParameter, RelationStatement parentRelationStatement) {

		fRightParameterNode = rightParameter;
		fParentRelationStatement = parentRelationStatement;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> choices) {

		String leftChoiceStr = getChoiceString(choices, fParentRelationStatement.getLeftParameter());
		if (leftChoiceStr == null) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		String rightChoiceStr = getChoiceString(choices, fRightParameterNode);
		if (rightChoiceStr == null) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		String substituteType = 
				JavaTypeHelper.getSubstituteType(fParentRelationStatement.getLeftParameter().getType(), fRightParameterNode.getType());

		EStatementRelation relation = fParentRelationStatement.getRelation();

		boolean isRandomizedChoice = StatementConditionHelper.getChoiceRandomized(choices, fParentRelationStatement.getLeftParameter());
		if(isRandomizedChoice) {
			if(JavaTypeHelper.TYPE_NAME_STRING.equals(substituteType)) {
				return EvaluationResult.convertFromBoolean(leftChoiceStr.matches(rightChoiceStr));
			}
			else {
				boolean result = StatementConditionHelper.isConstraintInChoiceRange(leftChoiceStr, rightChoiceStr, relation, substituteType);
				return EvaluationResult.convertFromBoolean(result);
			}
		}

		if (StatementConditionHelper.isRelationMatchQuiet(relation, substituteType, leftChoiceStr, rightChoiceStr)) {
			return EvaluationResult.TRUE;
		}

		return EvaluationResult.FALSE;
	}

	private static String getChoiceString(List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {

		ChoiceNode choiceNode = StatementConditionHelper.getChoiceForMethodParameter(choices, methodParameterNode);

		if (choiceNode == null) {
			return null;
		}

		return choiceNode.getValueString();
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public ParameterCondition getCopy() {

		return new ParameterCondition(fRightParameterNode.makeClone(), fParentRelationStatement);
	}

	@Override
	public boolean updateReferences(MethodNode methodNode) {

		MethodParameterNode tmpParameterNode = methodNode.getMethodParameter(fRightParameterNode.getName());
		if (tmpParameterNode == null) {
			return false;
		}
		fRightParameterNode = tmpParameterNode;

		return true;
	}

	@Override
	public Object getCondition(){

		return fRightParameterNode;
	}

	@Override
	public boolean compare(IStatementCondition otherCondition) {

		if (!(otherCondition instanceof ParameterCondition)) {
			return false;
		}

		ParameterCondition otherParamCondition = (ParameterCondition)otherCondition;

		if (fParentRelationStatement.getRelation() != otherParamCondition.fParentRelationStatement.getRelation()) {
			return false;
		}		

		if (!fRightParameterNode.isMatch(otherParamCondition.fRightParameterNode)) {
			return false;
		}

		return true;
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {

		return visitor.visit(this);
	}

	@Override
	public String toString() {

		return StatementConditionHelper.createParameterDescription(fRightParameterNode.getName());
	}

	@Override
	public boolean mentions(MethodParameterNode methodParameterNode) {

		if (fRightParameterNode == methodParameterNode) {
			return true;
		}

		return false;
	}	

	public MethodParameterNode getRightParameterNode() {

		return fRightParameterNode;
	}

	@Override
	public boolean isAmbigous(List<List<ChoiceNode>> domain, int parameterIndex,
			EStatementRelation relation) {
		String substituteType = JavaTypeHelper.getSubstituteType(fParentRelationStatement
				.getLeftParameter().getType(), JavaTypeHelper.getStringTypeName());

		if (substituteType == null || parameterIndex >= domain.size()) {
			return false;
		}

		List<ChoiceNode> values = domain.get(parameterIndex);
		int index2 = fRightParameterNode.getIndex();
		List<ChoiceNode> rightSideDomain = domain.get(index2);

		boolean isRandomizedChoice = StatementConditionHelper.getChoiceRandomized(values,
				fParentRelationStatement.getLeftParameter());

		if (isRandomizedChoice) {
			if (JavaTypeHelper.TYPE_NAME_STRING.equals(substituteType)) {
				return false;
			}

//			String leftChoiceStr = getChoiceString(values,
//					fParentRelationStatement.getLeftParameter());

			for (ChoiceNode left : values) {
				for (ChoiceNode right : rightSideDomain) {
					if (StatementConditionHelper.isAmbigous(left.getValueString(),
							right.getValueString(), relation, substituteType)) {
						return true;
					}
				}
			}

			/*
			 * else { boolean result =
			 * StatementConditionHelper.isAmbigous(leftChoiceStr, fRightValue,
			 * relation, substituteType); return result; } }
			 */
			/*
			 * if (StatementConditionHelper.isRelationMatchQuiet(relation,
			 * substituteType, leftChoiceStr, fRightValue)) { return true; }
			 */

		}
		return false;
	}
}	


