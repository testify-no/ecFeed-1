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

import static com.ecfeed.core.model.EStatementRelation.EQUAL;
import static com.ecfeed.core.model.EStatementRelation.GREATER_EQUAL;
import static com.ecfeed.core.model.EStatementRelation.GREATER_THAN;
import static com.ecfeed.core.model.EStatementRelation.LESS_EQUAL;
import static com.ecfeed.core.model.EStatementRelation.LESS_THAN;

import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.StringHelper;

public class StatementConditionHelper {

	private static final String TYPE_INFO_CHOICE = "choice";
	private static final String TYPE_INFO_PARAMETER = "parameter";
	private static final String TYPE_INFO_LABEL = "label";
	private static final int SINGLE_VALUE = 1;
	private static final int RANGE_VALUE = 2;

	public static ChoiceNode getChoiceForMethodParameter(List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {

		if (choices == null) {
			return null;
		}

		MethodNode methodNode = methodParameterNode.getMethod();
		if (methodNode == null) {
			return null;
		}

		int index = methodNode.getParameters().indexOf(methodParameterNode);		
		if (index == -1) {
			return null;
		}

		if(choices.size() < index + 1) {
			return null;
		}

		return choices.get(index);
	}

	public static String createChoiceDescription(String parameterName) {
		return parameterName + "[" + TYPE_INFO_CHOICE + "]";
	}

	public static String createParameterDescription(String parameterName) {
		return parameterName + "[" + TYPE_INFO_PARAMETER + "]";
	}

	public static String createLabelDescription(String parameterName) {
		return parameterName + "[" + TYPE_INFO_LABEL + "]";
	}	

	public static boolean containsNoTypeInfo(String string) {

		if (string.contains("[") &&  string.contains("]")) {
			return false;
		}

		return true;
	}

	public static boolean containsLabelTypeInfo(String string) {

		if (containsTypeInfo(string, TYPE_INFO_LABEL)) {
			return true;
		}

		return false;
	}

	public static String removeLabelTypeInfo(String string) {
		return removeTypeInfo(string, TYPE_INFO_LABEL);
	}

	public static boolean containsParameterTypeInfo(String string) {

		if (containsTypeInfo(string, TYPE_INFO_PARAMETER)) {
			return true;
		}

		return false;
	}

	public static String removeParameterTypeInfo(String string) {
		return removeTypeInfo(string, TYPE_INFO_PARAMETER);
	}

	public static boolean containsChoiceTypeInfo(String string) {

		if (containsTypeInfo(string, TYPE_INFO_CHOICE)) {
			return true;
		}

		return false;
	}	

	public static String removeChoiceTypeInfo(String string) {
		return removeTypeInfo(string, TYPE_INFO_CHOICE);
	}

	public static boolean isRelationMatchQuiet(EStatementRelation relation, String typeName, String leftString, String rightString) {

		boolean result = false;
		try {
			result = isRelationMatch(relation, typeName, leftString, rightString);
		} catch (Exception e) {
		}

		return result;
	}

	public static boolean isRelationMatch(
			EStatementRelation relation, String typeName, String leftString, String rightString) {

		if (typeName == null) {
			return false;
		}		

		if (relation == EStatementRelation.EQUAL && StringHelper.isEqual(leftString, rightString)) {
			return true;
		}
		if (relation == EStatementRelation.NOT_EQUAL && !StringHelper.isEqual(leftString, rightString)) {
			return true;
		}		

		if (JavaTypeHelper.isNumericTypeName(typeName)) {
			if (isMatchForNumericTypes(typeName, relation, leftString, rightString)) {
				return true;
			}
			return false;
		}

		if (JavaTypeHelper.isTypeWithChars(typeName)) {
			if (EStatementRelation.isMatch(relation, leftString, rightString)) {
				return true;
			}
			return false;
		}

		if (JavaTypeHelper.isBooleanTypeName(typeName)) {
			if (EStatementRelation.isEqualityMatchForBooleans(relation, leftString, rightString)) {
				return true;
			}
			return false;
		}		

		if (EStatementRelation.isEqualityMatch(relation, leftString, rightString)) {
			return true;
		}

		return false;
	}

	public static boolean getChoiceRandomized(List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {
		ChoiceNode choiceNode = getChoiceForMethodParameter(choices, methodParameterNode);

		if (choiceNode == null) {
			return false;
		}

		return choiceNode.isRandomizedValue();
	}

	public static boolean getChoiceRandomized(ChoiceNode choice, MethodParameterNode methodParameterNode) {
		return getChoiceRandomized(Arrays.asList(choice), methodParameterNode);
	}

	public static boolean isConstraintInChoiceRange(String choice, String constraint, EStatementRelation relation, String substituteType) {
		boolean result = false;
		if(JavaTypeHelper.isNumericTypeName(substituteType)) {		
			String[] choices = choice.split(":");
			String[] constraints = constraint.split(":");
			String lower;
			String upper;

			lower = choices[0];
			if (choices.length == SINGLE_VALUE) {
				upper = lower;
			}
			else {
				upper = choices[1];
			}

			//TODO
			//call the methods from StatemenetConditionHelper
			//e.g.: 	private static boolean isMatchForNumericTypes(
			//String typeName, EStatementRelation relation, String actualValue, String valueToMatch) {


			String lowerConstraint = constraints[0];
			String upperConstraint;
			if (constraints.length == SINGLE_VALUE) {
				upperConstraint = lowerConstraint;
			}
			else {
				upperConstraint = constraints[1];
			}

			if (!relation.equals(EQUAL)) {
				result = 
						StatementConditionHelper.validateOtherThanEqualCondition(
								relation, substituteType, lower, upper, lowerConstraint, upperConstraint);
			}
			else {
				result = 
						validateEqualCondition(
								choices.length, constraints.length, substituteType, 
								lower, upper, 
								lowerConstraint, upperConstraint);
			}
		}
		return result;
	}

	public static final boolean isAmbiguous(
			String choicesTxt, 
			String constraintsTxt, 
			EStatementRelation relation, 
			String substituteType,
			MessageStack outWhyAmbiguous) {

		if (!JavaTypeHelper.isNumericTypeName(substituteType)) {
			return false;
		}

		String[] choices = createRangeArray(choicesTxt);
		String[] constraints = createRangeArray(constraintsTxt);

		if (relation.equals(EQUAL)) {
			return isAmbiguousForEqualRelation(choices, constraints, substituteType);
		} else {
			return isAmbiguousForNonEqualRelation(choices, constraints, relation, substituteType);
		}
	}

	private static boolean isAmbiguousForNonEqualRelation(
			String[] choices, 
			String[] constraints,
			EStatementRelation relation,
			String substituteType) {

		boolean a = isRelationMatchQuiet(relation, substituteType, choices[0], constraints[0]);
		boolean b = isRelationMatchQuiet(relation, substituteType, choices[1], constraints[1]);
		boolean c = isRelationMatchQuiet(relation, substituteType, choices[0], constraints[1]);
		boolean d = isRelationMatchQuiet(relation, substituteType, choices[1], constraints[0]);

		if (a && b && c && d) {
			return false;
		} else {
			return (!(!a && !b && !c && !d));
		}
	}

	private static boolean isAmbiguousForEqualRelation(
			String[] choices, String[] constraints, String substituteType) {

		if (StringHelper.isEqual(choices[0], constraints[0]) && StringHelper.isEqual(choices[1], constraints[1])) {
			if (StringHelper.isEqual(choices[0], choices[1]) && StringHelper.isEqual(constraints[0], constraints[1])) {
				return false;
			}
			return true;
		}

		if (LESS_THAN.isMatch(substituteType, choices[0], constraints[0]) 
				&& LESS_THAN.isMatch(substituteType, choices[1], constraints[1]) 
				&& LESS_THAN.isMatch(substituteType, choices[1], constraints[0])) {
			return false;

		}

		if (GREATER_THAN.isMatch(substituteType, choices[0], constraints[0])
				&& GREATER_THAN.isMatch(substituteType, choices[0], constraints[1])
				&& GREATER_THAN.isMatch(substituteType, choices[1], constraints[1])) {
			return false;
		}

		return true;
	}

	public static String[] createRangeArray(String str) {

		String[] array = str.split(":");

		String lower = array[0];
		String upper = getUpperRange(array);

		return new String[]{ lower, upper };
	}

	private static boolean containsTypeInfo(String string, String typeDescription) {

		if (string.contains("[" + typeDescription + "]")) {
			return true;
		}

		return false;
	}

	private static String removeTypeInfo(String string, String typeDescription) {
		return StringHelper.removeFromPostfix("[" + typeDescription + "]", string);
	}

	private static boolean validateEqualCondition(
			int choicesLength, int constraintsLength,
			String substituteType, 
			String lower, String upper, 
			String lowerConstraint, String upperConstraint) {

		if (choicesLength == RANGE_VALUE && constraintsLength == RANGE_VALUE) {
			return GREATER_EQUAL.isMatch(substituteType, upper, lowerConstraint)
					&& LESS_EQUAL.isMatch(substituteType, lower, upperConstraint);
		}

		if (choicesLength == RANGE_VALUE && constraintsLength == SINGLE_VALUE) {
			return GREATER_EQUAL.isMatch(substituteType, lower, lowerConstraint)
					&& GREATER_EQUAL.isMatch(substituteType, upper, lowerConstraint);
		} 

		if (choicesLength == SINGLE_VALUE && constraintsLength == RANGE_VALUE) {
			return GREATER_EQUAL.isMatch(substituteType, lower, lowerConstraint)
					&& LESS_EQUAL.isMatch(substituteType, lower, upperConstraint);
		} 

		if (choicesLength == SINGLE_VALUE && constraintsLength == SINGLE_VALUE) {
			return EQUAL.isMatch(substituteType, lower, lowerConstraint);
		}

		return false;
	}

	private static boolean isMatchForNumericTypes(String typeName, EStatementRelation relation,
			String actualValue, String valueToMatch) {

		double actual = JavaTypeHelper.convertNumericToDouble(typeName, actualValue);
		double toMatch = JavaTypeHelper.convertNumericToDouble(typeName, valueToMatch);

		if (EStatementRelation.isMatch(relation, actual, toMatch)) {
			return true;
		}
		return false;
	}

	private static String getUpperRange(String[] array) {

		if (array.length == SINGLE_VALUE) {
			return array[0];
		}

		return array[1];
	}

	private static boolean validateOtherThanEqualCondition(
			EStatementRelation relation,
			String substituteType, 
			String lower,
			String upper,
			String lowerConstraint,
			String upperConstraint) {

		if (relation.isMatch(substituteType, lower, upperConstraint)) {
			return true;
		}

		if (relation.isMatch(substituteType, upper, lowerConstraint)) {
			return true;
		}

		return false;
	}

}

