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

public class ChoicesParentStatement extends AbstractStatement implements IRelationalStatement{

	private MethodParameterNode fParameter;
	private EStatementRelation fRelation;
	private ICondition fCondition;

	public interface ICondition {
		public Object getCondition();
		public boolean evaluate(List<ChoiceNode> values);
		public boolean adapt(List<ChoiceNode> values);
		public ICondition getCopy();
		public boolean updateReferences(MethodParameterNode parameter);
		public boolean compare(ICondition condition);
		public Object accept(IStatementVisitor visitor) throws Exception;
	}

	public ChoicesParentStatement(
			MethodParameterNode parameter, EStatementRelation relation, String labelCondition) {

		fParameter = parameter;
		fRelation = relation;
		fCondition = new LabelCondition(labelCondition);
	}

	public ChoicesParentStatement(
			MethodParameterNode parameter, EStatementRelation relation, ChoiceNode choiceCondition) {

		fParameter = parameter;
		fRelation = relation;
		fCondition = new ChoiceCondition(choiceCondition);
	}

	private ChoicesParentStatement(
			MethodParameterNode parameter, EStatementRelation relation, ICondition condition) {

		fParameter = parameter;
		fRelation = relation;
		fCondition = condition;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> values) {
		return fCondition.evaluate(values);
	}

	@Override
	public void setRelation(EStatementRelation relation) {
		fRelation = relation;
	}

	@Override
	public EStatementRelation getRelation() {
		return fRelation;
	}

	@Override
	public String getLeftOperandName() {
		return getParameter().getName();
	}

	@Override
	public String toString() {
		return getLeftOperandName() + getRelation() + fCondition.toString();
	}

	@Override
	public EStatementRelation[] getAvailableRelations() {
		return new EStatementRelation[]{EStatementRelation.EQUAL, EStatementRelation.NOT};
	}

	@Override
	public ChoicesParentStatement getCopy() {
		return new ChoicesParentStatement(fParameter, fRelation, fCondition.getCopy());
	}

	@Override
	public boolean updateReferences(MethodNode method) {

		MethodParameterNode parameter = (MethodParameterNode)method.getParameter(fParameter.getName());

		if (parameter != null && !parameter.isExpected()) {

			if (fCondition.updateReferences(parameter)) {
				fParameter = parameter;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean compare(IStatement statement) {

		if(statement instanceof ChoicesParentStatement == false){
			return false;
		}

		ChoicesParentStatement compared = (ChoicesParentStatement)statement;

		if(getParameter().getName().equals(compared.getParameter().getName()) == false) {
			return false;
		}

		if(getRelation() != compared.getRelation()) {
			return false;
		}

		return getCondition().compare(compared.getCondition());
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public boolean mentions(MethodParameterNode parameter) {
		return getParameter() == parameter;
	}

	@Override
	public boolean mentions(MethodParameterNode parameter, String label) {
		return getParameter() == parameter && getConditionValue().equals(label);
	}

	@Override
	public boolean mentions(ChoiceNode choice) {
		return getConditionValue() == choice;
	}


	public MethodParameterNode getParameter(){
		return fParameter;
	}

	public void setCondition(ICondition condition) {
		fCondition = condition;
	}

	public void setCondition(String label) {
		fCondition = new LabelCondition(label);
	}

	public void setCondition(ChoiceNode choice) {
		fCondition = new ChoiceCondition(choice);
	}

	public void setCondition(MethodParameterNode parameter, ChoiceNode choice) {
		fCondition = new ChoiceCondition(choice);
	}

	public ICondition getCondition() {
		return fCondition;
	}

	public Object getConditionValue() {
		return fCondition.getCondition();
	}

	public String getConditionName() {
		return fCondition.toString();
	}

	private ChoiceNode getChoiceForMethodParameter(List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {

		if (choices == null) {
			return null;
		}

		MethodNode methodNode = methodParameterNode.getMethod();

		if (methodNode == null) {
			return null;
		}

		int index = methodNode.getParameters().indexOf(methodParameterNode);

		if(choices.size() < index + 1) {
			return null;
		}

		return choices.get(index);
	}

	public class LabelCondition implements ICondition {

		private String fLabel;

		public LabelCondition(String label) {
			fLabel = label;
		}

		@Override
		public boolean evaluate(List<ChoiceNode> choices) {

			ChoiceNode choice = getChoiceForMethodParameter(choices, getParameter());

			if (choice == null) {
				return false;
			}

			return evaluateChoiceContainsLabel(choice);
		}

		private boolean evaluateChoiceContainsLabel(ChoiceNode choice) {

			boolean containsLabel = choice.getAllLabels().contains(fLabel);

			switch (getRelation()) {

			case EQUAL:
				return containsLabel;
			case NOT:
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
		public boolean compare(ICondition condition) {

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
			return fLabel + (fParameter.getAllChoiceNames().contains(fLabel)?"[label]":"");
		}

		@Override
		public LabelCondition getCopy() {
			return new LabelCondition(fLabel);
		}

		public String getLabel() {
			return fLabel;
		}

	}

	public class ChoiceCondition implements ICondition {

		private ChoiceNode fChoice;

		public ChoiceCondition(ChoiceNode choice) {
			fChoice = choice;
		}

		@Override
		public boolean evaluate(List<ChoiceNode> choices) {

			ChoiceNode choice = getChoiceForMethodParameter(choices, getParameter());

			if (choice == null) {
				return false;
			}

			return evaluateChoice(choice);
		}

		@Override
		public boolean adapt(List<ChoiceNode> values) {
			return false;
		}

		@Override
		public ChoiceCondition getCopy() {
			return new ChoiceCondition(fChoice.makeClone());
		}

		@Override
		public boolean updateReferences(MethodParameterNode parameter) {

			ChoiceNode condition = parameter.getChoice(fChoice.getQualifiedName());

			if (condition == null) {
				return false;
			}

			fChoice = condition;
			return true;
		}

		@Override
		public Object getCondition(){
			return fChoice;
		}

		@Override
		public boolean compare(ICondition condition) {

			if (condition instanceof ChoiceCondition == false) {
				return false;
			}

			ChoiceCondition compared = (ChoiceCondition)condition;

			return (fChoice.isMatch((ChoiceNode)compared.getCondition()));
		}

		@Override
		public Object accept(IStatementVisitor visitor) throws Exception {
			return visitor.visit(this);
		}

		@Override
		public String toString() {
			return fChoice.getQualifiedName();
		}

		public ChoiceNode getChoice() {
			return fChoice;
		}

		private boolean evaluateChoice(ChoiceNode choice) {

			boolean isCondition = choice.is(fChoice);
			EStatementRelation relation = getRelation(); 

			switch (relation) {

			case EQUAL:
				return isCondition;
			case NOT:
				return !isCondition;
			default:
				return false;
			}
		}

	}

}

