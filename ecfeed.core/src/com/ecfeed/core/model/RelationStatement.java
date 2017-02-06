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

	public RelationStatement(
			MethodParameterNode parameter, EStatementRelation relation, String labelCondition) {

		fParameter = parameter;
		fRelation = relation;
		fCondition = new LabelCondition(labelCondition, relation, parameter);
	}

	public RelationStatement(
			MethodParameterNode parameter, EStatementRelation relation, ChoiceNode choiceCondition) {

		fParameter = parameter;
		fRelation = relation;
		fCondition = new ChoiceCondition(choiceCondition, fParameter, relation);
	}

	public RelationStatement(
			MethodParameterNode parameter, EStatementRelation relation, MethodParameterNode rightParameter) {

		fParameter = parameter;
		fRelation = relation;
		fCondition = new ParameterCondition(rightParameter);
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

	public void setCondition(IStatementCondition condition) {
		fCondition = condition;
	}

	public void setCondition(String label) {
		fCondition = new LabelCondition(label, fRelation, fParameter);
	}

	public void setCondition(ChoiceNode choice) {
		fCondition = new ChoiceCondition(choice, fParameter, fRelation);
	}

	public void setCondition(MethodParameterNode parameter, ChoiceNode choice) {
		fCondition = new ChoiceCondition(choice, fParameter, fRelation);
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

