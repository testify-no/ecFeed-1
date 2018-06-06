package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.RangeHelper;


public class RangeHelperTest {

	@Test
	public void shouldCheckRange() {

		assertFalse(RangeHelper.isRange("0"));
		assertFalse(RangeHelper.isRange("0.0"));
		assertFalse(RangeHelper.isRange("A"));
		assertFalse(RangeHelper.isRange("A-B"));
		assertFalse(RangeHelper.isRange("A:B:C"));

		assertTrue(RangeHelper.isRange("0:0"));
		assertTrue(RangeHelper.isRange("1:4"));
		assertTrue(RangeHelper.isRange("1.0:4.0"));
		assertTrue(RangeHelper.isRange("X:Y"));
		assertTrue(RangeHelper.isRange("abc:XYZ"));
	}

	@Test
	public void shouldSplitRange() {
		String[] range = RangeHelper.splitToRange("x:y");
		assertEquals("x", range[0]);
		assertEquals("y", range[1]);
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowWhenSplitingInvalidRange() {

		RangeHelper.splitToRange("x:y:z");
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowWhenSplitingInvalidRange2() {

		RangeHelper.splitToRange("x");
	}	

	@Test
	public void shouldCreateRange() {
		assertEquals("0:0", RangeHelper.createRange("0"));
	}

	@Test
	public void shouldCreateRange2() {
		assertEquals("a:b", RangeHelper.createRange("a", "b"));
	}

	@Test
	public void testAmbiguousForEqualAndLong() {

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0:0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "0:0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "3:3", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"1:2", "2", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"1:2", "1:1", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"0:10", "9:100", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));		
	}

	@Test
	public void testAmbiguousForNotEqualAndLong() {

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0:0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "0:0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "3:3", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"1:2", "2", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"1:2", "1:1", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"0:10", "9:100", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));		
	}	

	@Test
	public void testAmbiguousForLessThanAndLong() {

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0:0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "0:0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "3:3", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"1:2", "2", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "1:1", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"0:10", "9:100", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));		
	}	

	@Test
	public void testAmbiguousForGreaterThanAndLong() {

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0:0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "0:0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "3:3", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "2", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"1:2", "1:1", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"0:10", "9:100", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));		
	}

	@Test
	public void testAmbiguousForLessEqualAndLong() {

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0:0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "0:0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "3:3", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "2", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"1:2", "1:1", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"0:10", "9:100", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));		
	}	

	@Test
	public void testAmbiguousForGreaterEqualAndLong() {

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0:0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"0:0", "0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "0:0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "3:3", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"1:2", "2", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "1:1", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"1:2", "0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"0:10", "9:100", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));		
	}

	@Test
	public void testAmbiguousForEqualAndDoubleWithSpecialValues() {

		assertFalse(RangeHelper.isAmbiguous(
				"100.0:POSITIVE_INFINITY", "99.0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_DOUBLE));

		assertTrue(RangeHelper.isAmbiguous(
				"100.0:POSITIVE_INFINITY", "150.0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_DOUBLE));

		assertTrue(RangeHelper.isAmbiguous(
				"100.0:200.0", "150.0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_DOUBLE));

		assertFalse(RangeHelper.isAmbiguous(
				"NEGATIVE_INFINITY:100.0", "101.0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_DOUBLE));

		assertTrue(RangeHelper.isAmbiguous(
				"NEGATIVE_INFINITY:100.0", "0.0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_DOUBLE));

		assertTrue(RangeHelper.isAmbiguous(
				"-200.0:100.0", "0.0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_DOUBLE));

		assertFalse(RangeHelper.isAmbiguous(
				"NEGATIVE_INFINITY:MIN_VALUE", "MAX_VALUE:POSITIVE_INFINITY", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_DOUBLE));

		assertTrue(RangeHelper.isAmbiguous(
				"NEGATIVE_INFINITY:MAX_VALUE", "MIN_VALUE:POSITIVE_INFINITY", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_DOUBLE));
	}

	@Test
	public void testAmbiguousForEqualAndLongWithSpecialValues() {
		testAmbiguousForEqualAndLongWithSpecialValues(EStatementRelation.EQUAL);
		testAmbiguousForEqualAndLongWithSpecialValues(EStatementRelation.NOT_EQUAL);
		testAmbiguousForEqualAndLongWithSpecialValues(EStatementRelation.LESS_EQUAL);
		testAmbiguousForEqualAndLongWithSpecialValues(EStatementRelation.LESS_THAN);
		testAmbiguousForEqualAndLongWithSpecialValues(EStatementRelation.GREATER_EQUAL);
		testAmbiguousForEqualAndLongWithSpecialValues(EStatementRelation.GREATER_THAN);
	}

	public void testAmbiguousForEqualAndLongWithSpecialValues(EStatementRelation statementRelation) {

		assertFalse(RangeHelper.isAmbiguous(
				"100:MAX_VALUE", "99", statementRelation, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"100:MAX_VALUE", "150", statementRelation, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"100:200", "150", statementRelation, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeHelper.isAmbiguous(
				"MIN_VALUE:100", "101", statementRelation, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"MIN_VALUE:100", "0", statementRelation, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeHelper.isAmbiguous(
				"-200:100", "0", statementRelation, JavaTypeHelper.TYPE_NAME_LONG));
	}

	@Test
	public void testForInvalidSpecialValues() {

		testForInvalidSpecialValues(EStatementRelation.EQUAL);
		testForInvalidSpecialValues(EStatementRelation.NOT_EQUAL);
		testForInvalidSpecialValues(EStatementRelation.LESS_EQUAL);
		testForInvalidSpecialValues(EStatementRelation.LESS_THAN);
		testForInvalidSpecialValues(EStatementRelation.GREATER_EQUAL);
		testForInvalidSpecialValues(EStatementRelation.GREATER_THAN);
	}

	public void testForInvalidSpecialValues(EStatementRelation statementRelation) {

		boolean wasException;

		wasException = false;
		try {
			RangeHelper.isAmbiguous(
					"NEGATIVE_INFINITY:100", "0", statementRelation, JavaTypeHelper.TYPE_NAME_LONG);
		} catch (Exception ex) {
			wasException = true;
		}

		assertTrue(wasException);

		wasException = false;
		try {
			RangeHelper.isAmbiguous(
					"100:POSITIVE_INFINITY", "0", statementRelation, JavaTypeHelper.TYPE_NAME_LONG);
		} catch (Exception ex) {
			wasException = true;
		}

		assertTrue(wasException);
	}

	@Test
	public void testCorrectRangeWithOneValueAndInteger() {
		String[] range = new String[]{ "1", "1" }; 
		assertTrue(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_INT));
	}

	@Test
	public void testCorrectRangeWithOneValueFloat() {
		String[] range = new String[]{ "1.0", "1.0" }; 
		assertTrue(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_DOUBLE));
	}	

	@Test
	public void testCorrectRangeWithInteger() {
		String[] range = new String[]{ "1", "2" }; 
		assertTrue(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_BYTE));
	}

	@Test
	public void testCorrectRangeWithFloat() {
		String[] range = new String[]{ "1.0", "2.0" }; 
		assertTrue(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_FLOAT));
	}	

	@Test
	public void testNotCorrectRangeWithInteger() {
		String[] range = new String[]{ "2", "1" }; 
		assertFalse(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_LONG));
	}	

	@Test
	public void testNotCorrectRangeWithFloat() {
		String[] range = new String[]{ "2.0", "1.0" }; 
		assertFalse(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_FLOAT));
	}

	@Test
	public void testCorrectStringTypes1() {
		String[] range = new String[]{ "xyz", "xyz" }; 
		assertTrue(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_STRING));
	}

	@Test
	public void testCorrectStringTypes2() {
		String[] range = new String[]{ "abc", "def" }; 
		assertTrue(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_STRING));
	}

	@Test
	public void testNotCorrectStringTypes() {
		String[] range = new String[]{ "def", "abc" }; 
		assertFalse(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_STRING));
	}

	@Test
	public void testForDoubleWithMinValue1() {
		String[] range = new String[]{ "MIN_VALUE", "0" }; 
		assertFalse(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_DOUBLE));
	}

	@Test
	public void testForDoubleWithMinValue2() {
		String[] range = new String[]{ "0", "MIN_VALUE" }; 
		assertTrue(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_DOUBLE));
	}	
	
	@Test
	public void testForDoubleWithMinMaxValue1() {
		String[] range = new String[]{ "MIN_VALUE", "MAX_VALUE" }; 
		assertTrue(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_DOUBLE));
	}	

	@Test
	public void testForIntWithMinValue1() {
		String[] range = new String[]{ "MIN_VALUE", "0" }; 
		assertTrue(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_INT));
	}	

	@Test
	public void testForIntWithMinMaxValue() {
		String[] range = new String[]{ "MIN_VALUE", "MAX_VALUE" }; 
		assertTrue(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_INT));
	}	
	
	@Test
	public void testForIntWithMinValue2() {
		String[] range = new String[]{ "0", "MIN_VALUE" }; 
		assertFalse(RangeHelper.isRangeCorrect(range, JavaTypeHelper.TYPE_NAME_INT));
	}	

}
