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

	private String fRightLabel;
	private RelationStatement fParentRelationStatement;

	public LabelCondition(String label, RelationStatement parentRelationStatement) {
		fRightLabel = label;
		fParentRelationStatement = parentRelationStatement;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> choices) {

		ChoiceNode choice = 
				StatementConditionHelper.getChoiceForMethodParameter(choices, fParentRelationStatement.getLeftParameter());

		if (choice == null) {
			return false;
		}

		return evaluateContainsLabel(choice);
	}

	@Override
	public boolean updateReferences(MethodNode methodNode) {

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
		return new LabelCondition(fRightLabel, fParentRelationStatement);
	}

	public String getRightLabel() {
		return fRightLabel;
	}

	@Override
	public boolean mentions(MethodParameterNode methodParameterNode) {

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

}

