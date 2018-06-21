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
import static com.ecfeed.core.model.EStatementRelation.LESS_EQUAL;

import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.utils.JavaTypeHelper;
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

