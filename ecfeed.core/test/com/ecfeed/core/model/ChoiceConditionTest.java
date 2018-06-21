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
import com.ecfeed.core.utils.MessageStack;

public class ChoiceConditionTest {

	enum AssertType {
		TRUE,
		FALSE,
	}

	public void evaluateOne(
			MethodParameterNode leftMethodParameterNode,
			String leftChoiceValue,
			EStatementRelation statementRelation,
			String rightChoiceValue,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod");
		methodNode.addParameter(leftMethodParameterNode);

		ChoiceNode rightChoiceNode = new ChoiceNode("Label" + rightChoiceValue , rightChoiceValue);
		rightChoiceNode.setParent(leftMethodParameterNode);

		RelationStatement statement = 
				RelationStatement.createStatementWithChoiceCondition(
						leftMethodParameterNode, statementRelation, rightChoiceNode);

		ChoiceNode leftChoiceNode = new ChoiceNode("Label" + leftChoiceValue, leftChoiceValue);
		leftChoiceNode.setParent(leftMethodParameterNode);

		EvaluationResult result = statement.evaluate(createList(leftChoiceNode));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}

	public void evaluateRandomizedOne(
			MethodParameterNode leftMethodParameterNode,
			String leftChoiceValue,
			EStatementRelation statementRelation,
			String rightChoiceValue,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod");
		methodNode.addParameter(leftMethodParameterNode);

		ChoiceNode rightChoiceNode = new ChoiceNode("Label" + rightChoiceValue , rightChoiceValue);
		rightChoiceNode.setParent(leftMethodParameterNode);

		rightChoiceNode.setRandomizedValue(true);
		RelationStatement statement = 
				RelationStatement.createStatementWithChoiceCondition(
						leftMethodParameterNode, statementRelation, rightChoiceNode);

		ChoiceNode leftChoiceNode = new ChoiceNode("Label" + leftChoiceValue, leftChoiceValue);
		leftChoiceNode.setRandomizedValue(true);

		leftChoiceNode.setParent(leftMethodParameterNode);

		EvaluationResult result = statement.evaluate(createList(leftChoiceNode));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}

	public void evaluateRandomizeAmbiguousOne(
			MethodParameterNode leftMethodParameterNode,
			String leftChoiceValue,
			EStatementRelation statementRelation,
			String rightChoiceValue,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod");
		methodNode.addParameter(leftMethodParameterNode);

		ChoiceNode rightChoiceNode = new ChoiceNode("Label" + rightChoiceValue , rightChoiceValue);
		rightChoiceNode.setParent(leftMethodParameterNode);

		rightChoiceNode.setRandomizedValue(true);
		RelationStatement statement = 
				RelationStatement.createStatementWithChoiceCondition(
						leftMethodParameterNode, statementRelation, rightChoiceNode);

		ChoiceNode leftChoiceNode = new ChoiceNode("Label" + leftChoiceValue, leftChoiceValue);
		leftChoiceNode.setRandomizedValue(true);

		leftChoiceNode.setParent(leftMethodParameterNode);

		EvaluationResult result = 
				EvaluationResult.convertFromBoolean(
						statement.isAmbiguous(Arrays.asList(createList(leftChoiceNode)), new MessageStack()));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}

	private List<ChoiceNode> createList(ChoiceNode choiceNode1) {
		return Arrays.asList(choiceNode1);
	}
	
	@Test
	public void evaluateForStrings() {

		MethodParameterNode leftParam = 
				new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_STRING, "", false);

		evaluateOne(leftParam, "a", EStatementRelation.EQUAL, "a", AssertType.TRUE);
		evaluateOne(leftParam, "a", EStatementRelation.EQUAL, "A", AssertType.FALSE);

		evaluateOne(leftParam, "a", EStatementRelation.EQUAL, "b", AssertType.FALSE);
		evaluateOne(leftParam, "a", EStatementRelation.NOT_EQUAL, "b", AssertType.TRUE);
		evaluateOne(leftParam, "a", EStatementRelation.LESS_THAN, "b", AssertType.TRUE);
		evaluateOne(leftParam, "a", EStatementRelation.LESS_EQUAL, "b", AssertType.TRUE);
		evaluateOne(leftParam, "a", EStatementRelation.GREATER_THAN, "b", AssertType.FALSE);
		evaluateOne(leftParam, "a", EStatementRelation.GREATER_EQUAL, "b", AssertType.FALSE);

		evaluateOne(leftParam, "abc", EStatementRelation.LESS_THAN, "abd", AssertType.TRUE);
		evaluateOne(leftParam, "abc", EStatementRelation.LESS_EQUAL, "abd", AssertType.TRUE);
		evaluateOne(leftParam, "abc", EStatementRelation.NOT_EQUAL, "abd", AssertType.TRUE);
		evaluateOne(leftParam, "abc", EStatementRelation.EQUAL, "abd", AssertType.FALSE);
	}

	public void evaluateForIntegerTypes(String parameterType) {

		MethodParameterNode leftParam = new MethodParameterNode("par1", parameterType, "", false);

		evaluateOne(leftParam, "1", EStatementRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateOne(leftParam, "1", EStatementRelation.NOT_EQUAL, "1", AssertType.FALSE);
		evaluateOne(leftParam, "1", EStatementRelation.EQUAL, "2", AssertType.FALSE);
		evaluateOne(leftParam, "1", EStatementRelation.NOT_EQUAL, "2", AssertType.TRUE);

		evaluateOne(leftParam, "1", EStatementRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateOne(leftParam, "1", EStatementRelation.LESS_EQUAL,    "2", AssertType.TRUE);
		evaluateOne(leftParam, "1", EStatementRelation.GREATER_THAN,  "2", AssertType.FALSE);
		evaluateOne(leftParam, "1", EStatementRelation.GREATER_EQUAL, "2", AssertType.FALSE);

		// For integer types allow greater values than type range e.g. 256 for Byte
		evaluateOne(leftParam, "99999", EStatementRelation.LESS_THAN, "100000", AssertType.TRUE); 

		evaluateOne(leftParam, "1", EStatementRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateOne(leftParam, "1", EStatementRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateOne(leftParam, "1", EStatementRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateOne(leftParam, "1", EStatementRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateOne(leftParam, "1", EStatementRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateOne(leftParam, "1", EStatementRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateOne(leftParam, "a", EStatementRelation.EQUAL,         "1", AssertType.FALSE);
		evaluateOne(leftParam, "a", EStatementRelation.NOT_EQUAL,     "1", AssertType.TRUE);
		evaluateOne(leftParam, "a", EStatementRelation.LESS_THAN,     "1", AssertType.FALSE);
		evaluateOne(leftParam, "a", EStatementRelation.LESS_EQUAL,    "1", AssertType.FALSE);
		evaluateOne(leftParam, "a", EStatementRelation.GREATER_THAN,  "1", AssertType.FALSE);
		evaluateOne(leftParam, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
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

	public void evaluateForFloatTypes(String parameterType) {

		MethodParameterNode leftParam = new MethodParameterNode("par1", parameterType, "", false);

		evaluateOne(leftParam, "1", EStatementRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EStatementRelation.EQUAL,   "1.0", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EStatementRelation.EQUAL,   "1", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EStatementRelation.EQUAL,   "1.000", AssertType.FALSE);
		evaluateOne(leftParam, "1234.5678", EStatementRelation.EQUAL,   "1234.5678", AssertType.TRUE);		

		evaluateOne(leftParam, "1",   EStatementRelation.NOT_EQUAL, "1",   AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EStatementRelation.NOT_EQUAL, "1.0", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EStatementRelation.EQUAL,     "2.0", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EStatementRelation.NOT_EQUAL, "2.0", AssertType.TRUE);

		evaluateOne(leftParam, "1", EStatementRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EStatementRelation.LESS_THAN,     "2.0", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EStatementRelation.LESS_EQUAL,    "2.0", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EStatementRelation.GREATER_THAN,  "2.0", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EStatementRelation.GREATER_EQUAL, "2.0", AssertType.FALSE);

		evaluateOne(leftParam, "99999", EStatementRelation.LESS_THAN, "100000", AssertType.TRUE);

		evaluateOne(leftParam, "1.0", EStatementRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EStatementRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EStatementRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EStatementRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EStatementRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EStatementRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateOne(leftParam, "a", EStatementRelation.EQUAL,         "1.0", AssertType.FALSE);
		evaluateOne(leftParam, "a", EStatementRelation.NOT_EQUAL,     "1.0", AssertType.TRUE);
		evaluateOne(leftParam, "a", EStatementRelation.LESS_THAN,     "1.0", AssertType.FALSE);
		evaluateOne(leftParam, "a", EStatementRelation.LESS_EQUAL,    "1.0", AssertType.FALSE);
		evaluateOne(leftParam, "a", EStatementRelation.GREATER_THAN,  "1.0", AssertType.FALSE);
		evaluateOne(leftParam, "a", EStatementRelation.GREATER_EQUAL, "1.0", AssertType.FALSE);		
	}	

	public void evaluateForAmbiguousIntegerTypes(String parameterType) {
		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", parameterType, "", false);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.EQUAL,     "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.EQUAL, "2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL, "2", AssertType.FALSE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.LESS_THAN,     "2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.LESS_EQUAL,    "2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.GREATER_THAN,  "2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.GREATER_EQUAL, "2", AssertType.FALSE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "99999", EStatementRelation.LESS_THAN, "100000", AssertType.FALSE); 

		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.NOT_EQUAL,     "a", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EStatementRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.EQUAL,         "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.NOT_EQUAL,     "1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.LESS_THAN,     "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.LESS_EQUAL,    "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.GREATER_THAN,  "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "0:10", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EStatementRelation.GREATER_EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-10:0", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_EQUAL, "0:10", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EStatementRelation.GREATER_THAN, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "10:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.GREATER_THAN, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EStatementRelation.EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EStatementRelation.NOT_EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.NOT_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.NOT_EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.NOT_EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.NOT_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.NOT_EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.NOT_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.NOT_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.NOT_EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EStatementRelation.LESS_EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "10:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EStatementRelation.LESS_THAN, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-10:0", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EStatementRelation.LESS_THAN, "1:2", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1:10", EStatementRelation.LESS_THAN, "2", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1:10", EStatementRelation.GREATER_THAN, "2", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "5:10", EStatementRelation.LESS_THAN, "1:4", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "5:10", EStatementRelation.LESS_THAN, "1", AssertType.FALSE);
	}


	@Test
	public void evaluateForAmbiguousInteger() {
		evaluateForAmbiguousIntegerTypes(JavaTypeHelper.TYPE_NAME_INT);
	}

	@Test
	public void evaluateForAmbiguousLong() {
		evaluateForAmbiguousIntegerTypes(JavaTypeHelper.TYPE_NAME_LONG);
	}	

	@Test
	public void evaluateForAmbiguousShort() {
		evaluateForAmbiguousIntegerTypes(JavaTypeHelper.TYPE_NAME_SHORT);
	}	

	@Test
	public void evaluateFoAmbiguousdByte() {
		evaluateForAmbiguousIntegerTypes(JavaTypeHelper.TYPE_NAME_BYTE);
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

		MethodParameterNode leftParam = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_BOOLEAN, "", false);

		evaluateOne(leftParam, "true", EStatementRelation.EQUAL, "true", AssertType.TRUE);
		evaluateOne(leftParam, "true", EStatementRelation.EQUAL, "false", AssertType.FALSE);

		evaluateOne(leftParam, "true", EStatementRelation.EQUAL, "x", AssertType.FALSE);
		evaluateOne(leftParam, "true", EStatementRelation.NOT_EQUAL, "x", AssertType.TRUE);
		evaluateOne(leftParam, "x", EStatementRelation.EQUAL, "true", AssertType.FALSE);
		evaluateOne(leftParam, "x", EStatementRelation.NOT_EQUAL, "false", AssertType.TRUE);

		evaluateOne(leftParam, "true", EStatementRelation.LESS_THAN, "true", AssertType.FALSE);
		evaluateOne(leftParam, "true", EStatementRelation.LESS_THAN, "false", AssertType.FALSE);
		evaluateOne(leftParam, "true", EStatementRelation.LESS_EQUAL, "true", AssertType.FALSE);
		evaluateOne(leftParam, "true", EStatementRelation.LESS_EQUAL, "false", AssertType.FALSE);

		evaluateOne(leftParam, "true", EStatementRelation.GREATER_THAN, "true", AssertType.FALSE);
		evaluateOne(leftParam, "true", EStatementRelation.GREATER_THAN, "false", AssertType.FALSE);
		evaluateOne(leftParam, "true", EStatementRelation.GREATER_EQUAL, "true", AssertType.FALSE);
		evaluateOne(leftParam, "true", EStatementRelation.GREATER_EQUAL, "false", AssertType.FALSE);
	}

	@Test
	public void evaluateChar() {

		MethodParameterNode leftParam = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_CHAR, "", false);

		evaluateOne(leftParam, "a", EStatementRelation.EQUAL, "a", AssertType.TRUE);
		evaluateOne(leftParam, "a", EStatementRelation.NOT_EQUAL, "a", AssertType.FALSE);

		evaluateOne(leftParam, "a", EStatementRelation.EQUAL, "b", AssertType.FALSE);
		evaluateOne(leftParam, "a", EStatementRelation.NOT_EQUAL, "b", AssertType.TRUE);

		evaluateOne(leftParam, "a", EStatementRelation.LESS_THAN, "b", AssertType.TRUE);
		evaluateOne(leftParam, "a", EStatementRelation.GREATER_THAN, "b", AssertType.FALSE);

		evaluateOne(leftParam, "a", EStatementRelation.LESS_EQUAL, "b", AssertType.TRUE);
		evaluateOne(leftParam, "a", EStatementRelation.GREATER_EQUAL, "b", AssertType.FALSE);

		evaluateOne(leftParam, "a", EStatementRelation.LESS_EQUAL, "a", AssertType.TRUE);
		evaluateOne(leftParam, "a", EStatementRelation.GREATER_EQUAL, "a", AssertType.TRUE);


		evaluateOne(leftParam, "b", EStatementRelation.EQUAL, "a", AssertType.FALSE);
		evaluateOne(leftParam, "b", EStatementRelation.NOT_EQUAL, "a", AssertType.TRUE);

		evaluateOne(leftParam, "b", EStatementRelation.LESS_THAN, "a", AssertType.FALSE);
		evaluateOne(leftParam, "b", EStatementRelation.GREATER_THAN, "a", AssertType.TRUE);

		evaluateOne(leftParam, "b", EStatementRelation.LESS_EQUAL, "a", AssertType.FALSE);
		evaluateOne(leftParam, "b", EStatementRelation.GREATER_EQUAL, "a", AssertType.TRUE);
	}	

	@Test
	public void copyAndEqualityTest() {
		MethodParameterNode leftParam = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_STRING, "", false);
		MethodParameterNode rightParam = new MethodParameterNode("par2", JavaTypeHelper.TYPE_NAME_STRING, "", false);

		RelationStatement statement = 
				RelationStatement.createStatementWithParameterCondition(
						leftParam, EStatementRelation.EQUAL, rightParam);

		RelationStatement copy = statement.getCopy();

		boolean result = statement.compare(copy);
		assertEquals(true, result);

	}

	@Test
	public void updateReferencesTest() {
		MethodNode method1 = new MethodNode("method1");
		MethodParameterNode method1LeftParameterNode = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_STRING, "", false);
		method1.addParameter(method1LeftParameterNode);
		MethodParameterNode method1RightParameterNode = new MethodParameterNode("par2", JavaTypeHelper.TYPE_NAME_STRING, "", false);
		method1.addParameter(method1RightParameterNode);

		RelationStatement statement = 
				RelationStatement.createStatementWithParameterCondition(
						method1LeftParameterNode, EStatementRelation.EQUAL, method1RightParameterNode);

		MethodNode method2 = new MethodNode("method2");
		MethodParameterNode method2LeftParameterNode = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_STRING, "", false);
		method2.addParameter(method2LeftParameterNode);
		MethodParameterNode method2RightParameterNode = new MethodParameterNode("par2", JavaTypeHelper.TYPE_NAME_STRING, "", false);
		method2.addParameter(method2RightParameterNode);


		ParameterCondition parameterCondition = (ParameterCondition)statement.getCondition();

		assertNotEquals(method2LeftParameterNode.hashCode(), statement.getLeftParameter().hashCode());
		assertNotEquals(method2RightParameterNode.hashCode(), parameterCondition.getRightParameterNode().hashCode());

		statement.updateReferences(method2);

		assertEquals(method2LeftParameterNode.hashCode(), statement.getLeftParameter().hashCode());
		assertEquals(method2RightParameterNode.hashCode(), parameterCondition.getRightParameterNode().hashCode());
	}	

}
