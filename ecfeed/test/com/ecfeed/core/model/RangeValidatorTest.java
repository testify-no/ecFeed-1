package com.ecfeed.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ecfeed.core.utils.JavaTypeHelper;

public class RangeValidatorTest {

	@Test
	public void testAmbiguousForEqualAndLong() {

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"0:10", "9:100", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));		
	}

	@Test
	public void testAmbiguousForNotEqualAndLong() {

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"0:10", "9:100", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));		
	}	

	@Test
	public void testAmbiguousForLessThanAndLong() {

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"0:10", "9:100", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));		
	}	

	@Test
	public void testAmbiguousForGreaterThanAndLong() {

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"0:10", "9:100", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));		
	}

	@Test
	public void testAmbiguousForLessEqualAndLong() {

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"0:10", "9:100", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));		
	}	

	@Test
	public void testAmbiguousForGreaterEqualAndLong() {

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeValidator.isAmbiguous(
				"0:10", "9:100", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));		
	}	


}
