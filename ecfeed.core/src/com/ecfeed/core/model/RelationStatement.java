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
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.SystemLogger;

public class RelationStatement extends AbstractStatement implements IRelationalStatement{

	private MethodParameterNode fLeftParameter;
	private EStatementRelation fRelation;
	private IStatementCondition fRightCondition;

	public static RelationStatement createStatementWithLabelCondition(
			MethodParameterNode parameter, EStatementRelation relation, String label) {

		RelationStatement relationStatement = new RelationStatement(parameter, relation, null);

		IStatementCondition condition = new LabelCondition(label, relationStatement);
		relationStatement.setCondition(condition);

		return relationStatement;
	}

	public static RelationStatement createStatementWithChoiceCondition(
			MethodParameterNode parameter, EStatementRelation relation, ChoiceNode choiceNode) {

		RelationStatement relationStatement = new RelationStatement(parameter, relation, null);

		IStatementCondition condition = new ChoiceCondition(choiceNode, relationStatement);
		relationStatement.setCondition(condition);

		return relationStatement;
	}

	public static RelationStatement createStatementWithParameterCondition(
			MethodParameterNode parameter, EStatementRelation relation, MethodParameterNode rightParameter) {

		RelationStatement relationStatement = new RelationStatement(parameter, relation, null);

		IStatementCondition condition = new ParameterCondition(rightParameter, relationStatement);
		relationStatement.setCondition(condition);

		return relationStatement;
	}	

	public static RelationStatement createStatementWithValueCondition(
			MethodParameterNode parameter, EStatementRelation relation, String textValue) {

		RelationStatement relationStatement = new RelationStatement(parameter, relation, null);

		IStatementCondition condition = new ValueCondition(textValue, relationStatement);
		relationStatement.setCondition(condition);

		return relationStatement;
	}	

	private RelationStatement(
			MethodParameterNode parameter, EStatementRelation relation, IStatementCondition condition) {

		fLeftParameter = parameter;
		fRelation = relation;
		fRightCondition = condition;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> values) {

		EvaluationResult result;
		try {
			result = fRightCondition.evaluate(values);
		} catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
			return EvaluationResult.FALSE;
		}

		return result;
	}

	@Override
	public boolean isAmbiguous(List<List<ChoiceNode>> values, MessageStack messageStack) {

		try {
			int index = fLeftParameter.getMyIndex();
			if (fRightCondition.isAmbiguous(values, index, fRelation, messageStack)) {
				final String message = "Relation [" + this.toString() + "].";
				messageStack.addMessage(message);
				return true;
			}
			return false;
		}
		catch (Exception e) {
			SystemLogger.logCatch(e.getMessage());
			return false;
		}
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

		return getLeftParameter().getName();
	}

	@Override
	public String toString() {

		return getLeftOperandName() + getRelation() + fRightCondition.toString();
	}

	@Override
	public EStatementRelation[] getAvailableRelations() {

		return EStatementRelation.getAvailableRelations(getLeftParameter().getType());
	}

	@Override
	public RelationStatement getCopy() {

		return new RelationStatement(fLeftParameter, fRelation, fRightCondition.getCopy());
	}

	@Override
	public boolean updateReferences(MethodNode methodNode) {

		MethodParameterNode tmpParameterNode = methodNode.getMethodParameter(fLeftParameter.getName());

		if (tmpParameterNode != null && !tmpParameterNode.isExpected()) {

			if (fRightCondition.updateReferences(methodNode)) {
				fLeftParameter = tmpParameterNode;
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

		if (getLeftParameter().getName().equals(compared.getLeftParameter().getName()) == false) {
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

		if (getLeftParameter() == methodParameterNode) {
			return true;
		}

		if (fRightCondition.mentions(methodParameterNode)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean mentions(MethodParameterNode parameter, String label) {

		return getLeftParameter() == parameter && getConditionValue().equals(label);
	}

	@Override
	public boolean mentionsParameterAndOrderRelation(MethodParameterNode parameter) {

		if (!(parameter.isMatch(fLeftParameter))) {
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

		MethodNode methodNode = fLeftParameter.getMethod();
		MethodParameterNode methodParameterNode = methodNode.getMethodParameter(methodParameterIndex);

		if (mentions(methodParameterNode)) {
			return true;
		}

		return false;
	}	

	public MethodParameterNode getLeftParameter(){
		return fLeftParameter;
	}

	public void setCondition(IStatementCondition condition) {
		fRightCondition = condition;
	}

	public void setCondition(String label) {
		fRightCondition = new LabelCondition(label, this);
	}

	public void setCondition(ChoiceNode choice) {
		fRightCondition = new ChoiceCondition(choice, this);
	}

	public void setCondition(MethodParameterNode parameter, ChoiceNode choice) {
		fRightCondition = new ChoiceCondition(choice, this);
	}

	public IStatementCondition getCondition() {
		return fRightCondition;
	}

	public Object getConditionValue() {
		return fRightCondition.getCondition();
	}

	public String getConditionName() {
		return fRightCondition.toString();
	}

}

