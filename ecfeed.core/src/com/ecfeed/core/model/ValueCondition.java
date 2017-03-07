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

import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.StringHelper;


public class ValueCondition implements IStatementCondition {

	private MethodParameterNode fLeftParameterNode;
	private String fRightValue;
	private RelationStatement fParentRelationStatement;

	public ValueCondition(
			MethodParameterNode parameter, String value, RelationStatement parentRelationStatement) {

		fLeftParameterNode = parameter;
		fRightValue = value;
		fParentRelationStatement = parentRelationStatement;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> choices) {

		String substituteType = 
				JavaTypeHelper.getSubstituteType(fLeftParameterNode.getType(), JavaTypeHelper.getStringTypeName());

		if (substituteType == null) {
			return false;
		}

		String leftChoiceStr = getChoiceString(choices, fLeftParameterNode);
		if (leftChoiceStr == null) {
			return false;
		}

		if (StatementConditionHelper.isRelationMatchQuiet(
				fParentRelationStatement.getRelation(), substituteType, leftChoiceStr, fRightValue)) {
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

	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public ValueCondition getCopy() {

		return new ValueCondition(fLeftParameterNode.makeClone(), new String(fRightValue), fParentRelationStatement);
	}

	@Override
	public boolean updateReferences(MethodParameterNode parameter) {

		return true;
	}

	@Override
	public Object getCondition(){

		return null;
	}

	@Override
	public boolean compare(IStatementCondition otherCondition) {

		if (!(otherCondition instanceof ValueCondition)) {
			return false;
		}

		ValueCondition otherValueCondition = (ValueCondition)otherCondition;

		if (!fLeftParameterNode.isMatch(otherValueCondition.fLeftParameterNode)) {
			return false;
		}

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

		if (fLeftParameterNode == methodParameterNode) {
			return true;
		}

		return false;
	}	

	public String getRightValue() {
		return fRightValue;
	}
}	


