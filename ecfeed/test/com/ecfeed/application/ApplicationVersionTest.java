package com.ecfeed.application;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ApplicationVersionTest{

	@Test
	public void test(){

		assertTrue(ApplicationVersionHelper.isThisNewerVersion("1.1.1", "1.1.0"));
		assertFalse(ApplicationVersionHelper.isThisNewerVersion("1.1.1", "1.1.2"));
		assertTrue(ApplicationVersionHelper.isThisNewerVersion("001.001.001", "1.1.0"));

		assertTrue(ApplicationVersionHelper.isThisNewerVersion("11.1.1", "2.1.1"));
		assertFalse(ApplicationVersionHelper.isThisNewerVersion("2.1.1", "11.1.1"));
	}

}
