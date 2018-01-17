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

public class ParameterConditionTest {

	enum AssertType {
		TRUE,
		FALSE,
	}

	public void evaluateOne(
			MethodParameterNode leftMethodParameterNode,
			MethodParameterNode rightMethodParameterNode,
			String leftChoiceValue,
			EStatementRelation statementRelation,
			String rightChoiceValue,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod");
		methodNode.addParameter(leftMethodParameterNode);
		methodNode.addParameter(rightMethodParameterNode);

		RelationStatement statement = 
				RelationStatement.createStatementWithParameterCondition(
						leftMethodParameterNode, statementRelation, rightMethodParameterNode);

		ChoiceNode leftChoiceNode = new ChoiceNode("Label" + leftChoiceValue, leftChoiceValue);
		ChoiceNode rightChoiceNode = new ChoiceNode("Label" + rightChoiceValue, rightChoiceValue);

		EvaluationResult result = statement.evaluate(createList(leftChoiceNode, rightChoiceNode));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}

	private List<ChoiceNode> createList(ChoiceNode choiceNode1, ChoiceNode choiceNode2) {
		return Arrays.asList(new ChoiceNode[]{choiceNode1, choiceNode2});
	}

	@Test
	public void evaluateForStrings() {

		MethodParameterNode leftParam = 
				new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_STRING, "", false);

		MethodParameterNode rightParam = 
				new MethodParameterNode("par2", JavaTypeHelper.TYPE_NAME_STRING, "", false);		

		evaluateOne(leftParam, rightParam, "a", EStatementRelation.EQUAL, "a", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.EQUAL, "A", AssertType.FALSE);

		evaluateOne(leftParam, rightParam, "a", EStatementRelation.EQUAL, "b", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.NOT_EQUAL, "b", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.LESS_THAN, "b", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.LESS_EQUAL, "b", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.GREATER_THAN, "b", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.GREATER_EQUAL, "b", AssertType.FALSE);

		evaluateOne(leftParam, rightParam, "abc", EStatementRelation.LESS_THAN, "abd", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "abc", EStatementRelation.LESS_EQUAL, "abd", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "abc", EStatementRelation.NOT_EQUAL, "abd", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "abc", EStatementRelation.EQUAL, "abd", AssertType.FALSE);
	}

	public void evaluateForIntegerTypes(String parameterType) {

		MethodParameterNode leftParam = new MethodParameterNode("par1", parameterType, "", false);
		MethodParameterNode rightParam = new MethodParameterNode("par2", parameterType, "", false);

		evaluateOne(leftParam, rightParam, "1", EStatementRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1", EStatementRelation.NOT_EQUAL, "1", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1", EStatementRelation.EQUAL, "2", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1", EStatementRelation.NOT_EQUAL, "2", AssertType.TRUE);

		evaluateOne(leftParam, rightParam, "1", EStatementRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1", EStatementRelation.LESS_EQUAL,    "2", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1", EStatementRelation.GREATER_THAN,  "2", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1", EStatementRelation.GREATER_EQUAL, "2", AssertType.FALSE);

		// For integer types allow greater values than type range e.g. 256 for Byte
		evaluateOne(leftParam, rightParam, "99999", EStatementRelation.LESS_THAN, "100000", AssertType.TRUE); 

		evaluateOne(leftParam, rightParam, "1", EStatementRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1", EStatementRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1", EStatementRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1", EStatementRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1", EStatementRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1", EStatementRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateOne(leftParam, rightParam, "a", EStatementRelation.EQUAL,         "1", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.NOT_EQUAL,     "1", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.LESS_THAN,     "1", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.LESS_EQUAL,    "1", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.GREATER_THAN,  "1", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.GREATER_EQUAL, "1", AssertType.FALSE);
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

		MethodParameterNode leftParam = new MethodParameterNode("par1", parameterType, "", false);
		MethodParameterNode rightParam = new MethodParameterNode("par2", parameterType, "", false);

		evaluateOne(leftParam, rightParam, "1", EStatementRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.EQUAL,   "1.0", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.EQUAL,   "1", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.EQUAL,   "1.000", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1234.5678", EStatementRelation.EQUAL,   "1234.5678", AssertType.TRUE);		

		evaluateOne(leftParam, rightParam, "1",   EStatementRelation.NOT_EQUAL, "1",   AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.NOT_EQUAL, "1.0", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.EQUAL,     "2.0", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.NOT_EQUAL, "2.0", AssertType.TRUE);

		evaluateOne(leftParam, rightParam, "1", EStatementRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.LESS_THAN,     "2.0", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.LESS_EQUAL,    "2.0", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.GREATER_THAN,  "2.0", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.GREATER_EQUAL, "2.0", AssertType.FALSE);

		evaluateOne(leftParam, rightParam, "99999", EStatementRelation.LESS_THAN, "100000", AssertType.TRUE);

		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "1.0", EStatementRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateOne(leftParam, rightParam, "a", EStatementRelation.EQUAL,         "1.0", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.NOT_EQUAL,     "1.0", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.LESS_THAN,     "1.0", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.LESS_EQUAL,    "1.0", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.GREATER_THAN,  "1.0", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.GREATER_EQUAL, "1.0", AssertType.FALSE);		
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
		MethodParameterNode rightParam = new MethodParameterNode("par2", JavaTypeHelper.TYPE_NAME_BOOLEAN, "", false);

		evaluateOne(leftParam, rightParam, "true", EStatementRelation.EQUAL, "true", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "true", EStatementRelation.EQUAL, "false", AssertType.FALSE);

		evaluateOne(leftParam, rightParam, "true", EStatementRelation.EQUAL, "x", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "true", EStatementRelation.NOT_EQUAL, "x", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "x", EStatementRelation.EQUAL, "true", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "x", EStatementRelation.NOT_EQUAL, "false", AssertType.TRUE);

		evaluateOne(leftParam, rightParam, "true", EStatementRelation.LESS_THAN, "true", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "true", EStatementRelation.LESS_THAN, "false", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "true", EStatementRelation.LESS_EQUAL, "true", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "true", EStatementRelation.LESS_EQUAL, "false", AssertType.FALSE);

		evaluateOne(leftParam, rightParam, "true", EStatementRelation.GREATER_THAN, "true", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "true", EStatementRelation.GREATER_THAN, "false", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "true", EStatementRelation.GREATER_EQUAL, "true", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "true", EStatementRelation.GREATER_EQUAL, "false", AssertType.FALSE);
	}

	@Test
	public void evaluateChar() {

		MethodParameterNode leftParam = new MethodParameterNode("par1", JavaTypeHelper.TYPE_NAME_CHAR, "", false);
		MethodParameterNode rightParam = new MethodParameterNode("par2", JavaTypeHelper.TYPE_NAME_CHAR, "", false);

		evaluateOne(leftParam, rightParam, "a", EStatementRelation.EQUAL, "a", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.NOT_EQUAL, "a", AssertType.FALSE);

		evaluateOne(leftParam, rightParam, "a", EStatementRelation.EQUAL, "b", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.NOT_EQUAL, "b", AssertType.TRUE);

		evaluateOne(leftParam, rightParam, "a", EStatementRelation.LESS_THAN, "b", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.GREATER_THAN, "b", AssertType.FALSE);

		evaluateOne(leftParam, rightParam, "a", EStatementRelation.LESS_EQUAL, "b", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.GREATER_EQUAL, "b", AssertType.FALSE);

		evaluateOne(leftParam, rightParam, "a", EStatementRelation.LESS_EQUAL, "a", AssertType.TRUE);
		evaluateOne(leftParam, rightParam, "a", EStatementRelation.GREATER_EQUAL, "a", AssertType.TRUE);


		evaluateOne(leftParam, rightParam, "b", EStatementRelation.EQUAL, "a", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "b", EStatementRelation.NOT_EQUAL, "a", AssertType.TRUE);

		evaluateOne(leftParam, rightParam, "b", EStatementRelation.LESS_THAN, "a", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "b", EStatementRelation.GREATER_THAN, "a", AssertType.TRUE);

		evaluateOne(leftParam, rightParam, "b", EStatementRelation.LESS_EQUAL, "a", AssertType.FALSE);
		evaluateOne(leftParam, rightParam, "b", EStatementRelation.GREATER_EQUAL, "a", AssertType.TRUE);
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
