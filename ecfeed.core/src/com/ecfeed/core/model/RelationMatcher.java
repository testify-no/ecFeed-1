package com.ecfeed.core.model;

import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.StringHelper;

public class RelationMatcher {

	public static boolean isMatchQuiet(
			EStatementRelation relation, String typeName, String leftString, String rightString) {

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

	private static boolean isMatchForNumericTypes(
			String typeName, EStatementRelation relation, String leftValue, String rightValue) {
		
		if (JavaTypeHelper.isLongTypeName(typeName)) {
			
			long leftLong = (long)JavaTypeHelper.parseLongValue(leftValue);
			long rightLong = (long)JavaTypeHelper.parseLongValue(rightValue);

			if (EStatementRelation.isMatch(relation, leftLong, rightLong)) {
				return true;
			}

			return false;
		}

		double leftDouble = JavaTypeHelper.convertNumericToDouble(typeName, leftValue);
		double rightDouble = JavaTypeHelper.convertNumericToDouble(typeName, rightValue);

		if (EStatementRelation.isMatch(relation, leftDouble, rightDouble)) {
			return true;
		}

		return false;
	}

}
