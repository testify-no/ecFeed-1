package com.ecfeed.core.model;

import static com.ecfeed.core.model.EStatementRelation.EQUAL;
import static com.ecfeed.core.model.EStatementRelation.NOT_EQUAL;
import static com.ecfeed.core.model.EStatementRelation.GREATER_THAN;
import static com.ecfeed.core.model.EStatementRelation.LESS_THAN;

import org.apache.commons.lang3.StringUtils;

import com.ecfeed.core.utils.JavaTypeHelper;

public class RangeAmbiguityValidator {

	private static final int SINGLE_VALUE = 1;

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

	public static String[] createRangeArray(String str) {

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
			String[] leftValues, 
			String[] rightValues, 
			String substituteType) {

		if (StringUtils.equals(leftValues[0], rightValues[0]) && StringUtils.equals(leftValues[1], rightValues[1])) {
			if (StringUtils.equals(leftValues[0], leftValues[1]) && StringUtils.equals(rightValues[0], rightValues[1])) {
				return false;
			}
			return true;
		}

		if (LESS_THAN.isMatch(substituteType, leftValues[0], rightValues[0]) 
				&& LESS_THAN.isMatch(substituteType, leftValues[1], rightValues[1]) 
				&& LESS_THAN.isMatch(substituteType, leftValues[1], rightValues[0])) {
			return false;

		}

		if (GREATER_THAN.isMatch(substituteType, leftValues[0], rightValues[0])
				&& GREATER_THAN.isMatch(substituteType, leftValues[0], rightValues[1])
				&& GREATER_THAN.isMatch(substituteType, leftValues[1], rightValues[1])) {
			return false;
		}

		return true;
	}

	private static boolean isAmbiguousForNonEqualRelation(
			String[] leftValues, 
			String[] rightValues,
			EStatementRelation relation,
			String substituteType) {

		boolean a = RelationMatcher.isMatchQuiet(relation, substituteType, leftValues[0], rightValues[0]);
		boolean b = RelationMatcher.isMatchQuiet(relation, substituteType, leftValues[1], rightValues[1]);
		boolean c = RelationMatcher.isMatchQuiet(relation, substituteType, leftValues[0], rightValues[1]);
		boolean d = RelationMatcher.isMatchQuiet(relation, substituteType, leftValues[1], rightValues[0]);

		if (a && b && c && d) {
			return false;
		} 

		if (a || b || c || d) {
			return true;
		}

		return false;
	}	
}
