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

import com.ecfeed.core.utils.SystemLogger;

public class RelationStatement extends AbstractStatement implements IRelationalStatement{

	private MethodParameterNode fParameter;
	private EStatementRelation fRelation;
	private IStatementCondition fCondition;

	public static RelationStatement createStatementWithLabelCondition(
			MethodParameterNode parameter, EStatementRelation relation, String label) {

		RelationStatement relationStatement = new RelationStatement(parameter, relation, null);

		IStatementCondition condition = new LabelCondition(parameter, label, relationStatement);
		relationStatement.setCondition(condition);

		return relationStatement;
	}

	public static RelationStatement createStatementWithChoiceCondition(
			MethodParameterNode parameter, EStatementRelation relation, ChoiceNode choiceNode) {

		RelationStatement relationStatement = new RelationStatement(parameter, relation, null);

		IStatementCondition condition = new ChoiceCondition(parameter, choiceNode, relationStatement);
		relationStatement.setCondition(condition);

		return relationStatement;
	}

	public static RelationStatement createStatementWithParameterCondition(
			MethodParameterNode parameter, EStatementRelation relation, MethodParameterNode rightParameter) {

		RelationStatement relationStatement = new RelationStatement(parameter, relation, null);

		IStatementCondition condition = new ParameterCondition(parameter, rightParameter, relationStatement);
		relationStatement.setCondition(condition);

		return relationStatement;
	}	

	public static RelationStatement createStatementWithValueCondition(
			MethodParameterNode parameter, EStatementRelation relation, String textValue) {

		RelationStatement relationStatement = new RelationStatement(parameter, relation, null);

		IStatementCondition condition = new ValueCondition(parameter, textValue, relationStatement);
		relationStatement.setCondition(condition);

		return relationStatement;
	}	

	private RelationStatement(
			MethodParameterNode parameter, EStatementRelation relation, IStatementCondition condition) {

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
	public boolean updateReferences(MethodNode methodNode) {

		MethodParameterNode methodParameterNode = (MethodParameterNode)methodNode.getParameter(fParameter.getName());

		if (methodParameterNode != null && !methodParameterNode.isExpected()) {

			if (fCondition.updateReferences(methodNode)) {
				fParameter = methodParameterNode;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean compare(IStatement statement) {

		if (statement instanceof RelationStatement == false) {
			return false;
		}

		RelationStatement compared = (RelationStatement)statement;

		if (getParameter().getName().equals(compared.getParameter().getName()) == false) {
			return false;
		}

		if (getRelation() != compared.getRelation()) {
			return false;
		}

		if (!getCondition().compare(compared.getCondition())) {
			return false;
		}

		return true;
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public boolean mentions(MethodParameterNode methodParameterNode) {

		if (getParameter() == methodParameterNode) {
			return true;
		}

		if (fCondition.mentions(methodParameterNode)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean mentions(MethodParameterNode parameter, String label) {

		return getParameter() == parameter && getConditionValue().equals(label);
	}

	@Override
	public boolean mentionsParameterAndOrderRelation(MethodParameterNode parameter) {

		if (!(parameter.isMatch(fParameter))) {
			return false;
		}

		if (EStatementRelation.isOrderRelation(fRelation)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean mentions(ChoiceNode choice) {

		return getConditionValue() == choice;
	}

	@Override
	public boolean mentions(int methodParameterIndex) {

		MethodNode methodNode = fParameter.getMethod();
		MethodParameterNode methodParameterNode = methodNode.getMethodParameter(methodParameterIndex);

		if (mentions(methodParameterNode)) {
			return true;
		}

		return false;
	}	

	public MethodParameterNode getParameter(){
		return fParameter;
	}

	public void setCondition(IStatementCondition condition) {
		fCondition = condition;
	}

	public void setCondition(String label) {
		fCondition = new LabelCondition(fParameter, label, this);
	}

	public void setCondition(ChoiceNode choice) {
		fCondition = new ChoiceCondition(fParameter, choice, this);
	}

	public void setCondition(MethodParameterNode parameter, ChoiceNode choice) {
		fCondition = new ChoiceCondition(fParameter, choice, this);
	}

	public IStatementCondition getCondition() {
		return fCondition;
	}

	public Object getConditionValue() {
		return fCondition.getCondition();
	}

	public String getConditionName() {
		return fCondition.toString();
	}

}

