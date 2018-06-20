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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.JavaTypeHelper;

public class ValueConditionTest {

	enum AssertType {
		TRUE,
		FALSE,
	}

	public void evaluateOne(
			MethodParameterNode methodParameterNode, 
			String choiceValue, 
			EStatementRelation statementRelation, 
			String value,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod");
		methodNode.addParameter(methodParameterNode);

		RelationStatement statement = 
				RelationStatement.createStatementWithValueCondition(
						methodParameterNode, statementRelation, value);

		ChoiceNode choiceNode = new ChoiceNode("Label" + choiceValue, choiceValue);

		EvaluationResult result = statement.evaluate(createList(choiceNode));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}


	private List<ChoiceNode> createList(ChoiceNode choiceNode) {
		return Arrays.asList(new ChoiceNode[]{choiceNode});
	}

	public void evaluateRandomizedOne(
			MethodParameterNode methodParameterNode, 
			String choiceValue, 
			EStatementRelation statementRelation, 
			String value,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod");
		methodNode.addParameter(methodParameterNode);

		RelationStatement statement = 
				RelationStatement.createStatementWithValueCondition(
						methodParameterNode, statementRelation, value);

		ChoiceNode choiceNode = new ChoiceNode("Label" + choiceValue, choiceValue);
		choiceNode.setRandomizeValue(true);
		

		EvaluationResult result = statement.evaluate(createList(choiceNode));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}
	
	public void evaluateRandomizeAmbigousOne(
			MethodParameterNode methodParameterNode, 
			String choiceValue, 
			EStatementRelation statementRelation, 
			String value,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod");
		methodNode.addParameter(methodParameterNode);

		RelationStatement statement = 
				RelationStatement.createStatementWithValueCondition(
						methodParameterNode, statementRelation, value);

		ChoiceNode choiceNode = new ChoiceNode("Label" + choiceValue, choiceValue);
		choiceNode.setRandomizeValue(true);
		
		

		EvaluationResult result = statement.isAmgibous(createList(choiceNode));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}
	
	
	public void evaluateForRangeIntegerTypes(String parameterType) {
		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", parameterType, "", false);

		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.EQUAL, "2", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL, "2", AssertType.TRUE);

		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.LESS_EQUAL,    "2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.GREATER_THAN,  "2", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.GREATER_EQUAL, "2", AssertType.FALSE);

		evaluateRandomizedOne(methodParameterNode, "99999", EStatementRelation.LESS_THAN, "100000", AssertType.TRUE); 

		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EStatementRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.EQUAL,         "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.NOT_EQUAL,     "1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.LESS_THAN,     "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.LESS_EQUAL,    "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.GREATER_THAN,  "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "0:10", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "2:2", EStatementRelation.GREATER_EQUAL, "2:2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-10:-1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "1:9", AssertType.TRUE);
		
		evaluateRandomizedOne(methodParameterNode, "2:2", EStatementRelation.GREATER_THAN, "2:2", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "10:100", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "11:100", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-10:-1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-10:0", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "1:9", AssertType.TRUE);
		
		evaluateRandomizedOne(methodParameterNode, "2:2", EStatementRelation.EQUAL, "2:2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "1:9", AssertType.TRUE);
		
		evaluateRandomizedOne(methodParameterNode, "2:2", EStatementRelation.LESS_EQUAL, "2:2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "11:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "1:9", AssertType.TRUE);
		
		evaluateRandomizedOne(methodParameterNode, "2:2", EStatementRelation.LESS_THAN, "2:2", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "10:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "11:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-10:-1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-10:0", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "1:9", AssertType.TRUE);
		
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		

		
		//tests from randomize-choice-value document
		evaluateRandomizedOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "1:2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1:10", EStatementRelation.LESS_THAN, "2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1:10", EStatementRelation.GREATER_THAN, "2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "5:10", EStatementRelation.LESS_THAN, "1:4", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "5:10", EStatementRelation.LESS_THAN, "1", AssertType.FALSE);
//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.LESS_THAN, "a-z", AssertType.FALSE);
//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.EQUAL, "a-z", AssertType.FALSE);
//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.EQUAL, "x", AssertType.FALSE);
	}
	
	@Test
	public void evaluateForRandomizedInteger() {
		evaluateForRangeIntegerTypes(JavaTypeHelper.TYPE_NAME_INT);
	}

	@Test
	public void evaluateForRandomizedLong() {
		evaluateForRangeIntegerTypes(JavaTypeHelper.TYPE_NAME_LONG);
	}	

	@Test
	public void evaluateForRandomizedShort() {
		evaluateForRangeIntegerTypes(JavaTypeHelper.TYPE_NAME_SHORT);
	}	

	@Test
	public void evaluateForRandomizedByte() {
		evaluateForRangeIntegerTypes(JavaTypeHelper.TYPE_NAME_BYTE);
	}	
	
	public void evaluateForAmbigousIntegerTypes(String parameterType) {
		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", parameterType, "", false);
		
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.EQUAL,     "1", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.EQUAL, "2", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL, "2", AssertType.FALSE);

		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.LESS_THAN,     "2", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.LESS_EQUAL,    "2", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.GREATER_THAN,  "2", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.GREATER_EQUAL, "2", AssertType.FALSE);

		evaluateRandomizeAmbigousOne(methodParameterNode, "99999", EStatementRelation.LESS_THAN, "100000", AssertType.FALSE); 

		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL,     "a", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1", EStatementRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.EQUAL,         "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.NOT_EQUAL,     "1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.LESS_THAN,     "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.LESS_EQUAL,    "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.GREATER_THAN,  "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "0:10", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "2:2", EStatementRelation.GREATER_EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-10:0", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "1:9", AssertType.TRUE);
		
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "0:10", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "2:2", EStatementRelation.GREATER_THAN, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "10:100", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "1:9", AssertType.TRUE);
		
		evaluateRandomizeAmbigousOne(methodParameterNode, "2:2", EStatementRelation.EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "1:9", AssertType.TRUE);
		
		evaluateRandomizeAmbigousOne(methodParameterNode, "2:2", EStatementRelation.LESS_EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "10:100", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "1:9", AssertType.TRUE);
		
		evaluateRandomizeAmbigousOne(methodParameterNode, "2:2", EStatementRelation.LESS_THAN, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-10:0", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "1:9", AssertType.TRUE);
	
		evaluateRandomizeAmbigousOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);

//		//tests from randomize-choice-value document
		evaluateRandomizeAmbigousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "1:2", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1:10", EStatementRelation.LESS_THAN, "2", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "1:10", EStatementRelation.GREATER_THAN, "2", AssertType.TRUE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "5:10", EStatementRelation.LESS_THAN, "1:4", AssertType.FALSE);
		evaluateRandomizeAmbigousOne(methodParameterNode, "5:10", EStatementRelation.LESS_THAN, "1", AssertType.FALSE);
//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.LESS_THAN, "a-z", AssertType.FALSE);
//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.EQUAL, "a-z", AssertType.FALSE);
//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.EQUAL, "x", AssertType.FALSE);
	}
	
	@Test
	public void evaluateForAmbigousInteger() {
		evaluateForAmbigousIntegerTypes(JavaTypeHelper.TYPE_NAME_INT);
	}

	@Test
	public void evaluateForAmbigousLong() {
		evaluateForAmbigousIntegerTypes(JavaTypeHelper.TYPE_NAME_LONG);
	}	

	@Test
	public void evaluateForAmbigousShort() {
		evaluateForAmbigousIntegerTypes(JavaTypeHelper.TYPE_NAME_SHORT);
	}	

	@Test
	public void evaluateFoAmbigousdByte() {
		evaluateForAmbigousIntegerTypes(JavaTypeHelper.TYPE_NAME_BYTE);
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void evaluateForStrings() {

		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_STRING, "", false);

		evaluateOne(methodParameterNode, "a", EStatementRelation.EQUAL, "a", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.EQUAL, "A", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EStatementRelation.EQUAL, "b", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.NOT_EQUAL, "b", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.LESS_THAN, "b", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.LESS_EQUAL, "b", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.GREATER_THAN, "b", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "b", AssertType.FALSE);

		evaluateOne(methodParameterNode, "abc", EStatementRelation.LESS_THAN, "abd", AssertType.TRUE);
		evaluateOne(methodParameterNode, "abc", EStatementRelation.LESS_EQUAL, "abd", AssertType.TRUE);
		evaluateOne(methodParameterNode, "abc", EStatementRelation.NOT_EQUAL, "abd", AssertType.TRUE);
		evaluateOne(methodParameterNode, "abc", EStatementRelation.EQUAL, "abd", AssertType.FALSE);
	}

	private void evaluateForIntegerTypes(String parameterType) {

		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", parameterType, "", false);

		evaluateOne(methodParameterNode, "1", EStatementRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL, "1", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EStatementRelation.EQUAL, "2", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL, "2", AssertType.TRUE);

		evaluateOne(methodParameterNode, "1", EStatementRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1", EStatementRelation.LESS_EQUAL,    "2", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1", EStatementRelation.GREATER_THAN,  "2", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EStatementRelation.GREATER_EQUAL, "2", AssertType.FALSE);

		// For integer types allow greater values than type range e.g. 256 for Byte
		evaluateOne(methodParameterNode, "99999", EStatementRelation.LESS_THAN, "100000", AssertType.TRUE); 

		evaluateOne(methodParameterNode, "1", EStatementRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1", EStatementRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EStatementRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EStatementRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EStatementRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EStatementRelation.EQUAL,         "1", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.NOT_EQUAL,     "1", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.LESS_THAN,     "1", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.LESS_EQUAL,    "1", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.GREATER_THAN,  "1", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
	}	

	@Test
	public void evaluateForInteger() {
		evaluateForIntegerTypes(JavaTypeHelper.TYPE_NAME_INT);
	}

	@Test
	public void evaluateForLong() {
		evaluateForIntegerTypes(JavaTypeHelper.TYPE_NAME_LONG);
	}	

	@Test
	public void evaluateForShort() {
		evaluateForIntegerTypes(JavaTypeHelper.TYPE_NAME_SHORT);
	}	

	@Test
	public void evaluateForByte() {
		evaluateForIntegerTypes(JavaTypeHelper.TYPE_NAME_BYTE);
	}	

	public void evaluateForFloatTypes(String parameterType) {

		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", parameterType, "", false);

		evaluateOne(methodParameterNode, "1", EStatementRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.EQUAL,   "1.0", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.EQUAL,   "1", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.EQUAL,   "1.000", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1234.5678", EStatementRelation.EQUAL,   "1234.5678", AssertType.TRUE);		

		evaluateOne(methodParameterNode, "1",   EStatementRelation.NOT_EQUAL, "1",   AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.NOT_EQUAL, "1.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.EQUAL,     "2.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.NOT_EQUAL, "2.0", AssertType.TRUE);

		evaluateOne(methodParameterNode, "1", EStatementRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.LESS_THAN,     "2.0", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.LESS_EQUAL,    "2.0", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.GREATER_THAN,  "2.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.GREATER_EQUAL, "2.0", AssertType.FALSE);

		evaluateOne(methodParameterNode, "99999", EStatementRelation.LESS_THAN, "100000", AssertType.TRUE);

		evaluateOne(methodParameterNode, "1.0", EStatementRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EStatementRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EStatementRelation.EQUAL,         "1.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.NOT_EQUAL,     "1.0", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.LESS_THAN,     "1.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.LESS_EQUAL,    "1.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.GREATER_THAN,  "1.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1.0", AssertType.FALSE);		
	}	

	@Test
	public void evaluateForFloat() {
		evaluateForFloatTypes(JavaTypeHelper.TYPE_NAME_FLOAT);
	}

	@Test
	public void evaluateForDouble() {
		evaluateForFloatTypes(JavaTypeHelper.TYPE_NAME_DOUBLE);
	}	

	@Test
	public void evaluateForBoolean() {

		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_BOOLEAN, "", false);

		evaluateOne(methodParameterNode, "true", EStatementRelation.EQUAL, "true", AssertType.TRUE);
		evaluateOne(methodParameterNode, "true", EStatementRelation.EQUAL, "false", AssertType.FALSE);

		evaluateOne(methodParameterNode, "true", EStatementRelation.EQUAL, "x", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EStatementRelation.NOT_EQUAL, "x", AssertType.TRUE);
		evaluateOne(methodParameterNode, "x", EStatementRelation.EQUAL, "true", AssertType.FALSE);
		evaluateOne(methodParameterNode, "x", EStatementRelation.NOT_EQUAL, "false", AssertType.TRUE);

		evaluateOne(methodParameterNode, "true", EStatementRelation.LESS_THAN, "true", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EStatementRelation.LESS_THAN, "false", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EStatementRelation.LESS_EQUAL, "true", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EStatementRelation.LESS_EQUAL, "false", AssertType.FALSE);

		evaluateOne(methodParameterNode, "true", EStatementRelation.GREATER_THAN, "true", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EStatementRelation.GREATER_THAN, "false", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EStatementRelation.GREATER_EQUAL, "true", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EStatementRelation.GREATER_EQUAL, "false", AssertType.FALSE);
	}

	@Test
	public void evaluateChar() {

		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_CHAR, "", false);

		evaluateOne(methodParameterNode, "a", EStatementRelation.EQUAL, "a", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.NOT_EQUAL, "a", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EStatementRelation.EQUAL, "b", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.NOT_EQUAL, "b", AssertType.TRUE);

		evaluateOne(methodParameterNode, "a", EStatementRelation.LESS_THAN, "b", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.GREATER_THAN, "b", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EStatementRelation.LESS_EQUAL, "b", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "b", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EStatementRelation.LESS_EQUAL, "a", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "a", AssertType.TRUE);


		evaluateOne(methodParameterNode, "b", EStatementRelation.EQUAL, "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "b", EStatementRelation.NOT_EQUAL, "a", AssertType.TRUE);

		evaluateOne(methodParameterNode, "b", EStatementRelation.LESS_THAN, "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "b", EStatementRelation.GREATER_THAN, "a", AssertType.TRUE);

		evaluateOne(methodParameterNode, "b", EStatementRelation.LESS_EQUAL, "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "b", EStatementRelation.GREATER_EQUAL, "a", AssertType.TRUE);
	}	

	@Test
	public void copyAndEqualityTest() {
		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_STRING, "", false);

		RelationStatement statement = 
				RelationStatement.createStatementWithValueCondition(
						methodParameterNode, EStatementRelation.EQUAL, "ABC");

		RelationStatement copy = statement.getCopy();

		boolean result = statement.compare(copy);
		assertEquals(true, result);
	}

	@Test
	public void updateReferencesTest() {
		MethodNode method1 = new MethodNode("method1");
		MethodParameterNode method1ParameterNode = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_STRING, "", false);
		method1.addParameter(method1ParameterNode);

		RelationStatement statement = 
				RelationStatement.createStatementWithValueCondition(
						method1ParameterNode, EStatementRelation.EQUAL, "ABC");

		MethodNode method2 = new MethodNode("method2");
		MethodParameterNode method2ParameterNode = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_STRING, "", false);
		method2.addParameter(method2ParameterNode);

		assertNotEquals(method2ParameterNode.hashCode(), statement.getLeftParameter().hashCode());

		statement.updateReferences(method2);

		assertEquals(method2ParameterNode.hashCode(), statement.getLeftParameter().hashCode());
	}	
}
