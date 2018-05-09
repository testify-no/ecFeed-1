package com.ecfeed.junit.staticrunner;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import com.ecfeed.core.utils.StringHelper;

public class LauncherForStaticRunnerTests {

	private static StringBuilder fLog;
	
	public static void appendToLog(String message) {
		
		if (fLog == null) {
			fLog = new StringBuilder();
		}
		fLog.append(message);
	}
	
	@Test
	public void shouldRunAllTestCases() {

		fLog = new StringBuilder();
		
		JUnitCore jUnitCore = new JUnitCore();
		Result result = jUnitCore.run(StaticRunnerTestAll.class);
		
		assertTrue(result.wasSuccessful());
		assertTrue(StringHelper.isEqual("B F(1,1) A B F(2,1) A", fLog.toString().trim()));
	}	
	
	@Test
	public void shouldRunSuite2() {
		
		fLog = new StringBuilder();
		
		JUnitCore jUnitCore = new JUnitCore();
		Result result = jUnitCore.run(StaticRunnerTestSuites.class);
		
		assertTrue(result.wasSuccessful());
		assertTrue(StringHelper.isEqual("B F(1,2) A B F(2,2) A", fLog.toString().trim()));
	}	
}

