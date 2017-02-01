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

	private String fLabel;
	private MethodParameterNode fParameter;
	private EStatementRelation fRelation;

	public LabelCondition(String label, EStatementRelation relation, MethodParameterNode parameter) {
		fLabel = label;
		fRelation = relation;
		fParameter = parameter;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> choices) {

		ChoiceNode choice = StatementConditionHelper.getChoiceForMethodParameter(choices, fParameter);

		if (choice == null) {
			return false;
		}

		return evaluateContainsLabel(choice);
	}

	private boolean evaluateContainsLabel(ChoiceNode choice) {

		boolean containsLabel = choice.getAllLabels().contains(fLabel);

		switch (fRelation) {

		case EQUAL:
			return containsLabel;
		case NOT_EQUAL:
			return !containsLabel;
		default:
			return false;
		}

	}

	@Override
	public boolean updateReferences(MethodParameterNode parameter) {
		return true;
	}

	@Override
	public Object getCondition(){
		return fLabel;
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
		return fLabel + "[label]";
	}

	@Override
	public LabelCondition getCopy() {
		return new LabelCondition(fLabel, fRelation, fParameter);
	}

	public String getLabel() {
		return fLabel;
	}

}

