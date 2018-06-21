package com.ecfeed.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.MessageStack;

public class RangeAmbiguityValidatorTest {

	@Test
	public void testAmbiguousForEqualAndLong() {

		MessageStack messageStack = new MessageStack();

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));		
	}

	@Test
	public void testAmbiguousForNotEqualAndLong() {

		MessageStack messageStack = new MessageStack();

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.NOT_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));
	}	

	@Test
	public void testAmbiguousForLessThanAndLong() {

		MessageStack messageStack = new MessageStack();

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.LESS_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));		
	}	

	@Test
	public void testAmbiguousForGreaterThanAndLong() {

		MessageStack messageStack = new MessageStack();

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.GREATER_THAN, JavaTypeHelper.TYPE_NAME_LONG, messageStack));		
	}

	@Test
	public void testAmbiguousForLessEqualAndLong() {

		MessageStack messageStack = new MessageStack();

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.LESS_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));		
	}	

	@Test
	public void testAmbiguousForGreaterEqualAndLong() {

		MessageStack messageStack = new MessageStack();

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0:0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"0:0", "0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0:0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "3:3", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertTrue(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "2", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "1:1", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));

		assertFalse(RangeAmbiguityValidator.isAmbiguous(
				"1:2", "0", EStatementRelation.GREATER_EQUAL, JavaTypeHelper.TYPE_NAME_LONG, messageStack));
	}	


}
