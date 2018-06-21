package com.ecfeed.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class RandomizedRangeHelperTest {

	@Test
	public void shouldCheckRange() {
		
		assertFalse(RandomizedRangeHelper.isRange("0"));
		assertFalse(RandomizedRangeHelper.isRange("0.0"));
		assertFalse(RandomizedRangeHelper.isRange("A"));
		assertFalse(RandomizedRangeHelper.isRange("A-B"));
		assertFalse(RandomizedRangeHelper.isRange("A:B:C"));
		
		assertTrue(RandomizedRangeHelper.isRange("0:0"));
		assertTrue(RandomizedRangeHelper.isRange("1:4"));
		assertTrue(RandomizedRangeHelper.isRange("1.0:4.0"));
		assertTrue(RandomizedRangeHelper.isRange("X:Y"));
		assertTrue(RandomizedRangeHelper.isRange("abc:XYZ"));
	}
	
	@Test
	public void shouldSplitRange() {
		String[] range = RandomizedRangeHelper.splitToRange("x:y");
		assertEquals("x", range[0]);
		assertEquals("y", range[1]);
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowWhenSplitingInvalidRange() {
		
		RandomizedRangeHelper.splitToRange("x:y:z");
	}
	
	@Test(expected = RuntimeException.class)
	public void shouldThrowWhenSplitingInvalidRange2() {
		
		RandomizedRangeHelper.splitToRange("x");
	}	

	@Test
	public void shouldCreateRange() {
		assertEquals("0:0", RandomizedRangeHelper.createRange("0"));
	}
	
	@Test
	public void shouldCreateRange2() {
		assertEquals("a:b", RandomizedRangeHelper.createRange("a", "b"));
	}
	
	
}
