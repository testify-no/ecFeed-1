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


public class ParameterCondition implements IStatementCondition {

	private MethodParameterNode fLeftParameterNode;
	private MethodParameterNode fRightParameterNode;
	private RelationStatement fParentRelationStatement;

	public ParameterCondition(
			MethodParameterNode parameter, MethodParameterNode rightParameter, RelationStatement parentRelationStatement) {

		fLeftParameterNode = parameter;
		fRightParameterNode = rightParameter;
		fParentRelationStatement = parentRelationStatement;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> choices) {

		String substituteType = 
				JavaTypeHelper.getSubstituteType(fLeftParameterNode.getType(), fRightParameterNode.getType());

		if (substituteType == null) {
			return false;
		}

		String leftChoice = 
				StatementConditionHelper.getChoiceForMethodParameter(choices, fLeftParameterNode).getValueString();

		String rightChoice = 
				StatementConditionHelper.getChoiceForMethodParameter(choices, fRightParameterNode).getValueString();

		if (StatementConditionHelper.isRelationMatchQuiet(fParentRelationStatement.getRelation(), substituteType, leftChoice, rightChoice)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public ParameterCondition getCopy() {
		return new ParameterCondition(fLeftParameterNode.makeClone(), fRightParameterNode.makeClone(), fParentRelationStatement);
	}

	@Override
	public boolean updateReferences(MethodParameterNode parameter) {
		return true;
	}

	@Override
	public Object getCondition(){
		return null; // TODO
	}

	@Override
	public boolean compare(IStatementCondition otherCondition) {

		if (!(otherCondition instanceof ParameterCondition)) {
			return false;
		}

		ParameterCondition otherParamCondition = (ParameterCondition)otherCondition;

		if (fLeftParameterNode != otherParamCondition.fLeftParameterNode) {
			return false;
		}

		if (fParentRelationStatement.getRelation() != otherParamCondition.fParentRelationStatement.getRelation()) {
			return false;
		}		

		if (fRightParameterNode != otherParamCondition.fRightParameterNode) {
			return false;
		}

		return true;
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	public String toString() {
		return StatementConditionHelper.createParameterDescription(fRightParameterNode.getName());
	}

	public MethodParameterNode getRightMethodParameterNode() {
		return fRightParameterNode;
	}	

}	


