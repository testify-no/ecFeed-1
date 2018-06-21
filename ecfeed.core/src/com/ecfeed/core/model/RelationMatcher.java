package com.ecfeed.core.model;

import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.StringHelper;

public class RelationMatcher {

	public static boolean isMatchQuiet(EStatementRelation relation, String typeName, String leftString, String rightString) {

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

	private static boolean isMatchForNumericTypes(String typeName, EStatementRelation relation,
			String actualValue, String valueToMatch) {

		double actual = JavaTypeHelper.convertNumericToDouble(typeName, actualValue);
		double toMatch = JavaTypeHelper.convertNumericToDouble(typeName, valueToMatch);

		if (EStatementRelation.isMatch(relation, actual, toMatch)) {
			return true;
		}
		return false;
	}	

}
