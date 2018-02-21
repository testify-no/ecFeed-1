package com.ecfeed.junit.onlinerunner;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import com.ecfeed.core.utils.StringHelper;

public class LauncherForOnlineRunnerTests {

	private static StringBuilder fLog;

	public static void appendToLog(String message) {

		if (fLog == null) {
			fLog = new StringBuilder();
		}
		fLog.append(message);
	}

	@Test
	public void shouldRunCartesianWithAnnotationBeforeClass() {

		fLog = new StringBuilder();

		JUnitCore jUnitCore = new JUnitCore();
		Result result = jUnitCore.run(OnlineRunnerTest1.class);

		assertTrue(result.wasSuccessful());
		assertTrue(StringHelper.isEqual("B F(1,1) A B F(1,2) A B F(2,1) A B F(2,2) A", fLog.toString().trim()));
	}

	@Test
	public void shouldRunCartesianWithAnnotationBeforeMethod() {

		fLog = new StringBuilder();

		JUnitCore jUnitCore = new JUnitCore();
		Result result = jUnitCore.run(OnlineRunnerTest2.class);

		assertTrue(result.wasSuccessful());
		assertTrue(StringHelper.isEqual("B F(1,1) A B F(1,2) A B F(2,1) A B F(2,2) A", fLog.toString().trim()));
	}	


}

