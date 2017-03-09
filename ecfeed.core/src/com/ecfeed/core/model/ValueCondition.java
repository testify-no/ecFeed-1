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

	//	private MethodParameterNode fLeftParameterNode; // XYX REMOVE
	private String fRightValue;
	private RelationStatement fParentRelationStatement;

	public ValueCondition(String rightValue, RelationStatement parentRelationStatement) {

		//		fLeftParameterNode = parentRelationStatement.getLeftParameter(); TUTAJ
		fRightValue = rightValue;
		fParentRelationStatement = parentRelationStatement;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> choices) {

		String substituteType = 
				JavaTypeHelper.getSubstituteType(fParentRelationStatement.getLeftParameter().getType(), JavaTypeHelper.getStringTypeName());

		if (substituteType == null) {
			return false;
		}

		String leftChoiceStr = getChoiceString(choices, fParentRelationStatement.getLeftParameter());
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


