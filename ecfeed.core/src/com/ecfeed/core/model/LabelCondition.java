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

public class LabelCondition implements IStatementCondition {

	private MethodParameterNode fLeftMethodParameterNode;
	private String fRightLabel;
	private RelationStatement fParentRelationStatement;

	public LabelCondition(MethodParameterNode methodParameterNode, String label, RelationStatement parentRelationStatement) {
		fRightLabel = label;
		fLeftMethodParameterNode = methodParameterNode;
		fParentRelationStatement = parentRelationStatement;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> choices) {

		ChoiceNode choice = StatementConditionHelper.getChoiceForMethodParameter(choices, fLeftMethodParameterNode);

		if (choice == null) {
			return false;
		}

		return evaluateContainsLabel(choice);
	}

	@Override
	public boolean updateReferences(MethodNode methodNode) {

		MethodParameterNode tmpParameterNode = methodNode.getMethodParameter(fLeftMethodParameterNode.getName());
		if (tmpParameterNode == null) {
			return false;
		}

		fLeftMethodParameterNode = tmpParameterNode;
		return true;
	}

	@Override
	public Object getCondition(){
		return fRightLabel;
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public boolean compare(IStatementCondition condition) {

		if(condition instanceof LabelCondition == false) {
			return false;
		}

		LabelCondition compared = (LabelCondition)condition;

		return (getCondition().equals(compared.getCondition()));
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return StatementConditionHelper.createLabelDescription(fRightLabel);
	}

	@Override
	public LabelCondition getCopy() {
		return new LabelCondition(fLeftMethodParameterNode, fRightLabel, fParentRelationStatement);
	}

	public String getRightLabel() {
		return fRightLabel;
	}

	@Override
	public boolean mentions(MethodParameterNode methodParameterNode) {

		if (fLeftMethodParameterNode == methodParameterNode) {
			return true;
		}

		return false;
	}

	private boolean evaluateContainsLabel(ChoiceNode choice) {

		boolean containsLabel = choice.getAllLabels().contains(fRightLabel);

		EStatementRelation relation = fParentRelationStatement.getRelation(); 

		switch (relation) {

		case EQUAL:
			return containsLabel;
		case NOT_EQUAL:
			return !containsLabel;
		default:
			return false;
		}

	}

	public MethodParameterNode getLeftParameterNode() {
		return fLeftMethodParameterNode;
	}



}

