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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.StringHelper;

class StatementRelationNames {
	static final String RELATION_EQUAL = "=";
	static final String RELATION_NOT_EQUAL = "\u2260";
	static final String RELATION_LESS_THAN = "<";
	static final String RELATION_LESS_EQUAL = "<=";
	static final String RELATION_GREATER_THAN = ">";
	static final String RELATION_GREATER_EQUAL = ">=";
}

public enum EStatementRelation {

	EQUAL(StatementRelationNames.RELATION_EQUAL), 
	NOT_EQUAL(StatementRelationNames.RELATION_NOT_EQUAL),
	LESS_THAN(StatementRelationNames.RELATION_LESS_THAN), 
	LESS_EQUAL(StatementRelationNames.RELATION_LESS_EQUAL),
	GREATER_THAN(StatementRelationNames.RELATION_GREATER_THAN),
	GREATER_EQUAL(StatementRelationNames.RELATION_GREATER_EQUAL);

	private String fName;

	private EStatementRelation(String name) {
		fName = name;
	}

	public String getName() {
		return fName; 
	}

	public String toString() {
		return fName; 
	}

	public static boolean isRelationEqual(String name) {

		if (name.equals(StatementRelationNames.RELATION_EQUAL)) {
			return true;
		}
		return false;
	}

	public static boolean isRelationNotEqual(String name) {

		if (name.equals(StatementRelationNames.RELATION_NOT_EQUAL)) {
			return true;
		}

		return false;
	}	

	public static boolean isOrderRelation(EStatementRelation relation) {

		if (isEquivalenceRelation(relation)) {
			return false;
		}

		return true;
	}

	public static boolean isEquivalenceRelation(EStatementRelation relation) {

		if (relation == EStatementRelation.EQUAL) {
			return true;
		}
		if (relation == EStatementRelation.NOT_EQUAL) {
			return true;
		}		

		return false;
	}	

	public static EStatementRelation getRelation(String name) {

		for (EStatementRelation relation : EStatementRelation.values()) {
			if (name.equals(relation.getName())) {
				return relation;
			}
		}

		return null;
	}

	public static EStatementRelation[] getAvailableRelations(String parameterType) {

		List<EStatementRelation> relations = new ArrayList<EStatementRelation>();

		for (EStatementRelation relation : EStatementRelation.values()) {
			if (isRelationForParameterType(relation, parameterType)) {
				relations.add(relation);
			}
		}

		return relations.toArray(new EStatementRelation[relations.size()]);
	}

	public static String[] getAvailableRelationNames(String parameterTypeName) {

		return relationCodesToNames(getAvailableRelations(parameterTypeName)); 
	}

	public static String[] relationCodesToNames(EStatementRelation[] relationCodes) {

		List<String> relationNames = new ArrayList<String>();

		for (EStatementRelation relation : relationCodes) {
			relationNames.add(relation.getName());
		}

		return relationNames.toArray(new String[relationNames.size()]);
	}

	public static boolean isRelationForParameterType(EStatementRelation relation, String parameterTypeName) {

		if (JavaTypeHelper.isTypeComparableForLessGreater(parameterTypeName)) {
			return true;
		}
		if (relation == EQUAL || relation == NOT_EQUAL) {
			return true;
		}
		return false;
	}

	public static boolean isMatch(EStatementRelation relation, double actualValue, double valueToMatch) {

		int compareResult = Double.compare(actualValue, valueToMatch);

		if (isMatch(relation, compareResult)) {
			return true;
		}
		return false;
	}

	public static boolean isMatch(EStatementRelation relation, String actualValue, String valueToMatch) {

		int compareResult = actualValue.compareTo(valueToMatch);

		if (isMatch(relation, compareResult)) {
			return true;
		}
		return false;
	}

	private static boolean isMatch(EStatementRelation relation, int compareResult) {

		switch(relation) {

		case EQUAL:
			return (compareResult == 0);

		case NOT_EQUAL:
			return (compareResult != 0);

		case LESS_THAN:
			return (compareResult < 0);

		case LESS_EQUAL:
			return (compareResult < 0 || compareResult == 0);

		case GREATER_THAN:
			return (compareResult > 0);

		case GREATER_EQUAL:
			return (compareResult > 0 || compareResult == 0);

		default:
			ExceptionHelper.reportRuntimeException("Invalid relation.");
			return false;
		}

	}

	public static boolean isEqualityMatch(EStatementRelation relation, String actualValue, String valueToMatch) {

		switch(relation) {

		case EQUAL:
			return StringHelper.isEqual(actualValue, valueToMatch);

		case NOT_EQUAL:
			return !(StringHelper.isEqual(actualValue, valueToMatch));

		default:
			ExceptionHelper.reportRuntimeException("Invalid relation: " + relation.toString() + " in match for equality.");
			return false;
		}
	}	
}
