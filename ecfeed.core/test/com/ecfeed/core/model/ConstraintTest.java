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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertEquals;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.utils.EvaluationResult;

public class ConstraintTest {
	@Test
	public void testEvaluate() {
		AbstractStatement trueStatement = new StaticStatement(true); 
		AbstractStatement falseStatement = new StaticStatement(false); 
		List<ChoiceNode> values = new ArrayList<ChoiceNode>();

		assertTrue(new Constraint(falseStatement, falseStatement).evaluate(values) == EvaluationResult.TRUE);
		assertTrue(new Constraint(falseStatement, trueStatement).evaluate(values) == EvaluationResult.TRUE);
		assertTrue(new Constraint(trueStatement, trueStatement).evaluate(values) == EvaluationResult.TRUE);
		assertTrue(new Constraint(trueStatement, falseStatement).evaluate(values) == EvaluationResult.FALSE);
	}

	@Test
	public void testSetPremise() {
		AbstractStatement statement1 = new StaticStatement(true); 
		AbstractStatement statement2 = new StaticStatement(false); 
		AbstractStatement statement3 = new StaticStatement(false);

		Constraint constraint = new Constraint(statement1, statement2);
		assertTrue(constraint.getPremise().equals(statement1));
		constraint.setPremise(statement3);
		assertTrue(constraint.getPremise().equals(statement3));
	}

	@Test
	public void testSetConsequence() {
		AbstractStatement statement1 = new StaticStatement(true); 
		AbstractStatement statement2 = new StaticStatement(false); 
		AbstractStatement statement3 = new StaticStatement(false);

		Constraint constraint = new Constraint(statement1, statement2);
		assertTrue(constraint.getConsequence().equals(statement2));
		constraint.setConsequence(statement3);
		assertTrue(constraint.getConsequence().equals(statement3));
	}

	@Test
	public void testMentions() {
		ChoiceNode choice = new ChoiceNode("choice", null);
		MethodParameterNode parameter = new MethodParameterNode("parameter", "type", "0", false);
		parameter.addChoice(choice);

		AbstractStatement mentioningStatement = 
				RelationStatement.createStatementWithChoiceCondition(parameter, EStatementRelation.EQUAL, choice);
		AbstractStatement notMentioningStatement = new StaticStatement(false);

		assertTrue(new Constraint(mentioningStatement, notMentioningStatement).mentions(parameter));
		assertTrue(new Constraint(mentioningStatement, notMentioningStatement).mentions(choice));

		assertTrue(new Constraint(notMentioningStatement, mentioningStatement).mentions(parameter));
		assertTrue(new Constraint(notMentioningStatement, mentioningStatement).mentions(choice));

		assertTrue(new Constraint(mentioningStatement, mentioningStatement).mentions(parameter));
		assertTrue(new Constraint(mentioningStatement, mentioningStatement).mentions(choice));

		assertFalse(new Constraint(notMentioningStatement, notMentioningStatement).mentions(parameter));
		assertFalse(new Constraint(notMentioningStatement, notMentioningStatement).mentions(choice));

	}

	@Test
	public void testTupleWithNullsForValueCondition() {

		AbstractStatement premise = createPremiseWithValueCondition();
		AbstractStatement consequence = createConsequenceWithValueCondition();

		Constraint constraint = new Constraint(premise, consequence);

		ChoiceNode choice1 = new ChoiceNode("choice1", "value1");
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2");

		evaluateConstraintWithNullValues(constraint, choice1, choice2);			
	}

	@Test
	public void testTupleWithNullsForChoiceCondition() {

		ChoiceNode choice1 = new ChoiceNode("choice1", "value1");
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2");

		AbstractStatement premise = createPremiseWithChoiceCondition(choice1);
		AbstractStatement consequence = createConsequenceWithChoiceCondition(choice2);

		Constraint constraint = new Constraint(premise, consequence);

		evaluateConstraintWithNullValues(constraint, choice1, choice2);			
	}
	
	@Test
	public void testTupleWithNullsForParameterCondition() {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "type", "0", false);
		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "type", "0", false);

		AbstractStatement premise = createStatementWithParameterCondition(parameter1, parameter2);
		AbstractStatement consequence = createStatementWithParameterCondition(parameter1, parameter2);

		Constraint constraint = new Constraint(premise, consequence);

		ChoiceNode choice1 = new ChoiceNode("choice1", "value1");
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2");

		evaluateConstraintWithNullValues(constraint, choice1, choice2);			
	}

	
	@Test
	public void testTupleWithNullConsequenceForChoiceCondition() {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "type", "0", false);
		ChoiceNode choice11 = new ChoiceNode("choice11", "value11");
		ChoiceNode choice12 = new ChoiceNode("choice12", "value12");
		choice11.setParent(parameter1);
		choice12.setParent(parameter1);

		AbstractStatement premise = 
				RelationStatement.createStatementWithChoiceCondition(
						parameter1, EStatementRelation.EQUAL, choice11);

		
		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "type", "0", false);
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2");
		choice2.setParent(parameter2);
		
		AbstractStatement consequence = 
				RelationStatement.createStatementWithChoiceCondition(
						parameter2, EStatementRelation.EQUAL, choice2);

		MethodNode methodNode = new MethodNode("methodNode");
		methodNode.addParameter(parameter1);
		methodNode.addParameter(parameter2);
		parameter1.setParent(methodNode);
		parameter2.setParent(methodNode);
		
		Constraint constraint = new Constraint(premise, consequence);

		List<ChoiceNode> values = new ArrayList<ChoiceNode>();
		values.add(choice12);
		values.add(null);
		assertTrue(constraint.evaluate(values) == EvaluationResult.TRUE);
	}
	
	
	private void evaluateConstraintWithNullValues(Constraint constraint, ChoiceNode choice1, ChoiceNode choice2) {

		List<ChoiceNode> values = new ArrayList<ChoiceNode>();
		values.add(null);
		values.add(null);
		assertTrue(constraint.evaluate(values) == EvaluationResult.INSUFFICIENT_DATA);

		values.clear();
		values.add(choice1);
		values.add(null);
		assertTrue(constraint.evaluate(values) == EvaluationResult.INSUFFICIENT_DATA);

		values.clear();
		values.add(null);
		values.add(choice2);
		assertTrue(constraint.evaluate(values) == EvaluationResult.INSUFFICIENT_DATA);
	}

	private AbstractStatement createPremiseWithValueCondition() {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "type", "0", false);

		AbstractStatement premise = 
				RelationStatement.createStatementWithValueCondition(
						parameter1, EStatementRelation.EQUAL, "A");

		return premise;
	}

	private AbstractStatement createConsequenceWithValueCondition() {

		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "type", "0", false);

		AbstractStatement consequence = 
				RelationStatement.createStatementWithValueCondition(
						parameter2, EStatementRelation.EQUAL, "C");

		return consequence;
	}

	private AbstractStatement createPremiseWithChoiceCondition(ChoiceNode choiceNode) {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "type", "0", false);

		AbstractStatement premise = 
				RelationStatement.createStatementWithChoiceCondition(
						parameter1, EStatementRelation.EQUAL, choiceNode);

		return premise;
	}

	private AbstractStatement createConsequenceWithChoiceCondition(ChoiceNode choiceNode) {

		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "type", "0", false);

		AbstractStatement consequence = 
				RelationStatement.createStatementWithChoiceCondition(
						parameter2, EStatementRelation.EQUAL, choiceNode);

		return consequence;
	}

	private AbstractStatement createStatementWithParameterCondition(
			MethodParameterNode parameter1, MethodParameterNode parameter2) {

		AbstractStatement premise = 
				RelationStatement.createStatementWithParameterCondition(
						parameter1, EStatementRelation.EQUAL, parameter2);

		return premise;
	}

}
