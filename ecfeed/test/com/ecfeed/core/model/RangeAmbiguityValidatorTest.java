package com.ecfeed.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ecfeed.core.utils.JavaTypeHelper;

public class RangeAmbiguityValidatorTest {

	@Test
	public void testAmbiguousForEqualAndLong() {

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG));		
	}

	@Test
	public void testAmbiguousForNotEqualAndLong() {

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));
	}	

	@Test
	public void testAmbiguousForLessThanAndLong() {

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG));		
	}	

	@Test
	public void testAmbiguousForGreaterThanAndLong() {

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG));		
	}

	@Test
	public void testAmbiguousForLessEqualAndLong() {

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));		
	}	

	@Test
	public void testAmbiguousForGreaterEqualAndLong() {

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG));
	}	


}
