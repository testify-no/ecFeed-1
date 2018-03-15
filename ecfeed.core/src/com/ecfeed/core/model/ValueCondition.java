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
import com.ecfeed.core.utils.StringHelper;


public class ValueCondition implements IStatementCondition {

	private String fRightValue;
	private RelationStatement fParentRelationStatement;

	public ValueCondition(String rightValue, RelationStatement parentRelationStatement) {

		fRightValue = rightValue;
		fParentRelationStatement = parentRelationStatement;
	}

	//TODO refactor + isAmbiguous
	private static boolean isConstraintInChoiceRange(String choice, String constraint, EStatementRelation relation, String substituteType) {
		boolean result = false;
		if(substituteType.equals("int") || substituteType.equals("long")) {		
			
			String[] choices = choice.split(":");
			String[] constraints = constraint.split(":");
			
			String lower;
			String upper;
			
			lower = choices[0];
			
			if (choices.length == 1) {
				upper = lower;
			}
			else {
				 upper = choices[1];
			}
			
			//TODO
			//call the methods from StatemenetConditionHelper
			//e.g.: 	private static boolean isMatchForNumericTypes(
			//String typeName, EStatementRelation relation, String actualValue, String valueToMatch) {

			
			String lowerConstraint = constraints[0];
			String upperConstraint;
			
			if (constraints.length == 1) {
				upperConstraint = lowerConstraint;
			}
			else {
				upperConstraint = constraints[1];
			}/*
			
			switch (substituteType) {
			case "=": 
				result = isValueInRange(lower, lowerConstraint, upperConstraint)
				|| isValueInRange(upper, lowerConstraint, upperConstraint);
				break;
			case "<":
				result = isValueInRange(lower, lowerConstraint, upperConstraint-1)
				|| isValueInRange(upper, lowerConstraint, upperConstraint-1);
				break;
			case ">":
				break;
			}*/
			
			result = 
			StatementConditionHelper.isRelationMatchQuiet(relation, substituteType, lower, lowerConstraint)
			||
			StatementConditionHelper.isRelationMatchQuiet(relation, substituteType, lower, upperConstraint)
			||
			StatementConditionHelper.isRelationMatchQuiet(relation, substituteType, upper, lowerConstraint)
			||
			StatementConditionHelper.isRelationMatchQuiet(relation, substituteType, upper, upperConstraint);
		}
		return result;
	}
	
	private static boolean isValueInRange(int value, int min, int max) {
		return value>=min || value <=max;
	}
	
	private static boolean isValueInRange(long value, long min, long max) {
		return value>=min || value <=max;
	}
	
	private static boolean isValueInRange(float value, float min, float max) {
		return Float.compare(value, min) > 0 || Float.compare(value, max) < 0;
	}
	
	private static boolean isValueInRange(double value, double min, double max) {
		return Double.compare(value, min) > 0 || Double.compare(value, max) < 0;
	}
	
	@Override
	public EvaluationResult evaluate(List<ChoiceNode> choices) {

		String substituteType = 
				JavaTypeHelper.getSubstituteType(
						fParentRelationStatement.getLeftParameter().getType(), JavaTypeHelper.getStringTypeName());

		if (substituteType == null) {
			return EvaluationResult.FALSE;
		}

		String leftChoiceStr = getChoiceString(choices, fParentRelationStatement.getLeftParameter());
		

		
		if (leftChoiceStr == null) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}
		//TODO 433

		
		EStatementRelation relation = fParentRelationStatement.getRelation();
		
		boolean isRandomizedChoice = getChoiceRandomized(choices, fParentRelationStatement.getLeftParameter());
		if(isRandomizedChoice) {
			if("String".equals(substituteType)) {
				//check does string match with regex
			}
			else {
				boolean result = isConstraintInChoiceRange(leftChoiceStr, fRightValue, relation, substituteType);
				return EvaluationResult.convertFromBoolean(result);
			}
		}

		if (StatementConditionHelper.isRelationMatchQuiet(relation, substituteType, leftChoiceStr, fRightValue)) {
			return EvaluationResult.TRUE;
		}

		return EvaluationResult.FALSE;
	}

	//TODO on thy fly version, needs to be refactored
	private static boolean getChoiceRandomized(List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {
		ChoiceNode choiceNode = StatementConditionHelper.getChoiceForMethodParameter(choices, methodParameterNode);

		if (choiceNode == null) {
			return false;
		}

		return choiceNode.isRandomizeValue();
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
	public ValueCondition getCopy() {

		return new ValueCondition(new String(fRightValue), fParentRelationStatement);
	}

	@Override
	public boolean updateReferences(MethodNode methodNode) {

		return true;
	}

	@Override
	public Object getCondition(){

		return fRightValue;
	}

	@Override
	public boolean compare(IStatementCondition otherCondition) {

		if (!(otherCondition instanceof ValueCondition)) {
			return false;
		}

		ValueCondition otherValueCondition = (ValueCondition)otherCondition;

		if (fParentRelationStatement.getRelation() != otherValueCondition.fParentRelationStatement.getRelation()) {
			return false;
		}		

		if (!StringHelper.isEqual(fRightValue, otherValueCondition.fRightValue)) {
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

		return fRightValue;
	}

	@Override
	public boolean mentions(MethodParameterNode methodParameterNode) {

		return false;
	}	

	public String getRightValue() {
		return fRightValue;
	}
}	


