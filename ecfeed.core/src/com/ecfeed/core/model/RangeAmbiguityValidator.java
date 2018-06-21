package com.ecfeed.core.model;

import static com.ecfeed.core.model.EStatementRelation.EQUAL;
import static com.ecfeed.core.model.EStatementRelation.GREATER_THAN;
import static com.ecfeed.core.model.EStatementRelation.LESS_THAN;

import org.apache.commons.lang3.StringUtils;

import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.MessageStack;

public class RangeAmbiguityValidator {

	private static final int SINGLE_VALUE = 1;

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

	private static boolean isAmbiguousForEqualRelation(
			String[] choices, String[] constraints, String substituteType) {

		if (StringUtils.equals(choices[0], constraints[0]) && StringUtils.equals(choices[1], constraints[1])) {
			if (StringUtils.equals(choices[0], choices[1]) && StringUtils.equals(constraints[0], constraints[1])) {
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

	private static boolean isAmbiguousForNonEqualRelation(
			String[] choices, 
			String[] constraints,
			EStatementRelation relation,
			String substituteType) {

		boolean a = RelationMatcher.isMatchQuiet(relation, substituteType, choices[0], constraints[0]);
		boolean b = RelationMatcher.isMatchQuiet(relation, substituteType, choices[1], constraints[1]);
		boolean c = RelationMatcher.isMatchQuiet(relation, substituteType, choices[0], constraints[1]);
		boolean d = RelationMatcher.isMatchQuiet(relation, substituteType, choices[1], constraints[0]);

		if (a && b && c && d) {
			return false;
		} else {
			return (!(!a && !b && !c && !d));
		}
	}	
}
