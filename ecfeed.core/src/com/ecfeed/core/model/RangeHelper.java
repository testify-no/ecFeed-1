package com.ecfeed.core.model;

import static com.ecfeed.core.model.EStatementRelation.EQUAL;
import static com.ecfeed.core.model.EStatementRelation.GREATER_EQUAL;
import static com.ecfeed.core.model.EStatementRelation.LESS_EQUAL;
import static com.ecfeed.core.model.EStatementRelation.LESS_THAN;
import static com.ecfeed.core.model.EStatementRelation.NOT_EQUAL;

import org.apache.commons.lang3.StringUtils;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaTypeHelper;

public class RangeHelper {

	private static final int SINGLE_VALUE = 1;
	private static final int RANGE_VALUE = 2;	

	public static final String DELIMITER = ":";

	public static boolean isRange(String value) {

		String[] values = value.split(DELIMITER);

		if (values.length != 2) {
			return false;
		}

		return true;
	}

	public static boolean isRangeCorrect(String[] range, String typeName) {

		if (RelationMatcher.isRelationMatch(
				EStatementRelation.LESS_EQUAL, typeName, range[0], range[1])) {
			return true;
		}

		return false;
	}

	public static String[] splitToRange(String value) {

		String[] range = value.split(DELIMITER);

		if (range.length != 2) {
			ExceptionHelper.reportRuntimeException("Invalid format of adapted value.");
		}

		return range;
	}

	public static String createRange(String value) {
		return createRange(value, value);
	}

	public static String createRange(String firstValue, String secondValue) {
		return firstValue + DELIMITER + secondValue;
	}	

	public static final boolean isAmbiguous(
			String leftRange, 
			String rightRange, 
			EStatementRelation relation, 
			String substituteType) {

		if (JavaTypeHelper.TYPE_NAME_STRING.equals(substituteType)) {
			return leftRange.matches(rightRange);
		}

		if (!JavaTypeHelper.isNumericTypeName(substituteType)) {
			return false;
		}

		String[] leftValues = createRangeArray(leftRange);
		String[] rightValues = createRangeArray(rightRange);

		if (isAmbiguousIntr(leftValues, rightValues, relation, substituteType)) {
			return true;
		}

		return false;
	}

	private static boolean isAmbiguousIntr(
			String[] leftValues,
			String[] rightValues,
			EStatementRelation relation,
			String substituteType) {

		if (relation.equals(EQUAL) || relation.equals(NOT_EQUAL)) {
			return isAmbiguousForEqualityRelations(
					leftValues, rightValues, substituteType);
		} else {
			return isAmbiguousForNonEqualRelation(
					leftValues, rightValues, relation, substituteType);
		}
	}

	private static String[] createRangeArray(String str) {

		String[] array = str.split(":");

		String lower = array[0];
		String upper = getUpperRange(array);

		return new String[]{ lower, upper };
	}

	private static String getUpperRange(String[] array) {

		if (array.length == SINGLE_VALUE) {
			return array[0];
		}

		return array[1];
	}

	private static boolean isAmbiguousForEqualityRelations(
			String[] leftRange, String[] rightRange, String substituteType) {

		if (areRangesTheSame(leftRange, rightRange)) {

			if (isSingleValue(leftRange)) {
				return false;
			}

			return true;
		}

		if (LESS_THAN.isMatch(substituteType, leftRange[1], rightRange[0])) {
			return false;
		}

		if (LESS_THAN.isMatch(substituteType, rightRange[1], leftRange[0])) {
			return false;
		}

		return true;
	}

	private static boolean isSingleValue(String[] range) {

		if (StringUtils.equals(range[0], range[1])) {
			return true;
		}

		return false;
	}

	private static boolean areRangesTheSame(String[] leftRange, String[] rightRange) {

		if (!StringUtils.equals(leftRange[0], rightRange[0])) {
			return false;
		}

		if (!StringUtils.equals(leftRange[1], rightRange[1])) {
			return false;
		}

		return true;
	}

	private static boolean isAmbiguousForNonEqualRelation(
			String[] leftValues, 
			String[] rightValues,
			EStatementRelation relation,
			String substituteType) {

		boolean a = RelationMatcher.isRelationMatch(relation, substituteType, leftValues[0], rightValues[0]);
		boolean b = RelationMatcher.isRelationMatch(relation, substituteType, leftValues[1], rightValues[1]);
		boolean c = RelationMatcher.isRelationMatch(relation, substituteType, leftValues[0], rightValues[1]);
		boolean d = RelationMatcher.isRelationMatch(relation, substituteType, leftValues[1], rightValues[0]);

		if (a && b && c && d) {
			return false;
		} 

		if (a || b || c || d) {
			return true;
		}

		return false;
	}

	public static boolean isRightRangeInLeftRange(
			String leftValues, String rightValues, EStatementRelation relation, String substituteType) {

		if(!JavaTypeHelper.isNumericTypeName(substituteType)) {
			return false;
		}

		String[] choices = createRangeArray(leftValues);
		String[] constraints = createRangeArray(rightValues);

		if (relation.equals(EQUAL)) {
			return validateEqualCondition(choices, constraints, substituteType);
		}

		return validateOtherThanEqualCondition(choices, constraints, relation, substituteType);
	}


	private static boolean validateEqualCondition(
			String[] choices, String[] constraints, String substituteType) {

		String lowerChoice = choices[0]; 
		String upperChoice = choices[1];

		String lowerConstraint = constraints[0];  
		String upperConstraint = constraints[1];

		int choicesLength = choices.length;
		int constraintsLength = constraints.length;

		if (choicesLength == RANGE_VALUE && constraintsLength == RANGE_VALUE) {
			return GREATER_EQUAL.isMatch(substituteType, upperChoice, lowerConstraint)
					&& LESS_EQUAL.isMatch(substituteType, lowerChoice, upperConstraint);
		}

		if (choicesLength == RANGE_VALUE && constraintsLength == SINGLE_VALUE) {
			return GREATER_EQUAL.isMatch(substituteType, lowerChoice, lowerConstraint)
					&& GREATER_EQUAL.isMatch(substituteType, upperChoice, lowerConstraint);
		} 

		if (choicesLength == SINGLE_VALUE && constraintsLength == RANGE_VALUE) {
			return GREATER_EQUAL.isMatch(substituteType, lowerChoice, lowerConstraint)
					&& LESS_EQUAL.isMatch(substituteType, lowerChoice, upperConstraint);
		} 

		if (choicesLength == SINGLE_VALUE && constraintsLength == SINGLE_VALUE) {
			return EQUAL.isMatch(substituteType, lowerChoice, lowerConstraint);
		}

		return false;
	}	

	private static boolean validateOtherThanEqualCondition(
			String[] choices,
			String[] constraints,
			EStatementRelation relation,
			String substituteType) {

		if (relation.isMatch(substituteType, choices[0], constraints[1])) {
			return true;
		}

		if (relation.isMatch(substituteType, choices[1], constraints[0])) {
			return true;
		}

		return false;
	}	
}
