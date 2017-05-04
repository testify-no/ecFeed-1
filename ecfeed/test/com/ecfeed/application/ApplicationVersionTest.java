package com.ecfeed.application;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ApplicationVersionTest{

	@Test
	public void test(){
		
		assertTrue(ApplicationVersion.isThisNewerVersion("1.1.1", "1.1.0"));
		assertFalse(ApplicationVersion.isThisNewerVersion("1.1.1", "1.1.2"));
		assertTrue(ApplicationVersion.isThisNewerVersion("001.001.001", "1.1.0"));
		
		assertTrue(ApplicationVersion.isThisNewerVersion("11.1.1", "2.1.1"));
		assertFalse(ApplicationVersion.isThisNewerVersion("2.1.1", "11.1.1"));
	}

}
