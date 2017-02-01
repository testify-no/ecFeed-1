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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SystemLogger;

public class RelationStatement extends AbstractStatement implements IRelationalStatement{

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

	public RelationStatement(
			MethodParameterNode parameter, EStatementRelation relation, String labelCondition) {

		fParameter = parameter;
		fRelation = relation;
		fCondition = new LabelCondition(labelCondition);
	}

	public RelationStatement(
			MethodParameterNode parameter, EStatementRelation relation, ChoiceNode choiceCondition) {

		fParameter = parameter;
		fRelation = relation;
		fCondition = new ChoiceCondition(choiceCondition);
	}

	private RelationStatement(
			MethodParameterNode parameter, EStatementRelation relation, ICondition condition) {

		fParameter = parameter;
		fRelation = relation;
		fCondition = condition;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> values) {

		boolean result;
		try {
			result = fCondition.evaluate(values);
		} catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
			return false;
		}

		return result;
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

		return EStatementRelation.getAvailableRelations(getParameter().getType());
	}

	@Override
	public RelationStatement getCopy() {

		return new RelationStatement(fParameter, fRelation, fCondition.getCopy());
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

		if(statement instanceof RelationStatement == false){
			return false;
		}

		RelationStatement compared = (RelationStatement)statement;

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
	public boolean mentionsOrderRelation() {

		return EStatementRelation.isOrderRelation(fRelation);
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

			return evaluateContainsLabel(choice);
		}

		private boolean evaluateContainsLabel(ChoiceNode choice) {

			boolean containsLabel = choice.getAllLabels().contains(fLabel);

			switch (getRelation()) {

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
			return fLabel + "[label]";
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

		private boolean evaluateChoice(ChoiceNode actualChoice) {

			EStatementRelation relation = getRelation();

			if (relation == EStatementRelation.EQUAL || relation == EStatementRelation.NOT_EQUAL) {
				return evaluateEqualityIncludingParents(relation, actualChoice);
			}

			String typeName = actualChoice.getParameter().getType();

			String actualValue = JavaTypeHelper.convertValueString(actualChoice.getValueString(), typeName);
			String valueToMatch = JavaTypeHelper.convertValueString(fChoice.getValueString(), typeName);

			return isDirectMatch(typeName, relation, actualValue, valueToMatch);
		}

		private boolean evaluateEqualityIncludingParents(EStatementRelation relation, ChoiceNode choice) {

			boolean isMatch = choice.isMatchIncludingParents(fChoice);

			switch (relation) {

			case EQUAL:
				return isMatch;
			case NOT_EQUAL:
				return !isMatch;
			default:
				ExceptionHelper.reportRuntimeException("Invalid relation.");
				return false;
			}
		}

		private boolean isDirectMatch(
				String typeName, EStatementRelation relation, String actualValue, String valueToMatch) {

			if (JavaTypeHelper.isNumericTypeName(typeName)) {
				return isMatchForNumericTypes(typeName, relation, actualValue, valueToMatch);
			}

			if (JavaTypeHelper.isTypeWithChars(typeName)) {
				return EStatementRelation.isMatch(relation, actualValue, valueToMatch);
			}

			return EStatementRelation.isEqualityMatch(relation, actualValue, valueToMatch);
		}

		private boolean isMatchForNumericTypes(
				String typeName, EStatementRelation relation, String actualValue, String valueToMatch) {

			double actual = JavaTypeHelper.convertNumericToDouble(typeName, actualValue);
			double toMatch = JavaTypeHelper.convertNumericToDouble(typeName, valueToMatch);

			return EStatementRelation.isMatch(relation, actual, toMatch);
		}

	}


	public class ParameterCondition implements ICondition {

		private MethodParameterNode fMethodParameterNode;

		public ParameterCondition(MethodParameterNode methodParameterNode) {
			fMethodParameterNode = methodParameterNode;
		}

		@Override
		public boolean evaluate(List<ChoiceNode> choices) {

			// TODO
			return false;
		}

		@Override
		public boolean adapt(List<ChoiceNode> values) {
			return false;
		}

		@Override
		public ParameterCondition getCopy() {
			return new ParameterCondition(fMethodParameterNode.makeClone());
		}

		@Override
		public boolean updateReferences(MethodParameterNode parameter) {
			return true;
		}

		@Override
		public Object getCondition(){
			return fMethodParameterNode;
		}

		@Override
		public boolean compare(ICondition otherCondition) {

			if (!(otherCondition instanceof ParameterCondition)) {
				return false;
			}

			ParameterCondition otherParamCondition = (ParameterCondition)otherCondition;

			return (fMethodParameterNode == otherParamCondition.fMethodParameterNode);
		}

		@Override
		public Object accept(IStatementVisitor visitor) throws Exception {
			return visitor.visit(this);
		}

		@Override
		public String toString() {
			return fMethodParameterNode.getName() + "[parameter]"; 
		}

		public MethodParameterNode getMethodParameterNode() {
			return fMethodParameterNode;
		}

	}	

}

