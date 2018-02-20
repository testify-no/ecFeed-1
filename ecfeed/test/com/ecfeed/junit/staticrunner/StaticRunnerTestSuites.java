package com.ecfeed.junit.staticrunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ecfeed.junit.StaticRunner;
import com.ecfeed.junit.annotations.EcModel;
import com.ecfeed.junit.annotations.TestSuites;

@RunWith(StaticRunner.class)
@EcModel("test/com/ecfeed/junit/staticrunner/StaticRunnerTestSuites.ect")
public class StaticRunnerTestSuites {
	
	@Before
	public void beforeTestMethod() {
		LauncherForStaticRunnerTests.appendToLog("B ");
	}

	@Test
	@TestSuites("Suite2")
	public void staticTest(int arg1, int arg2) {
		LauncherForStaticRunnerTests.appendToLog("F(" + arg1 + "," + arg2 + ") ");
	}

	@After
	public void afterTestMethod() {
		LauncherForStaticRunnerTests.appendToLog("A ");
	}

}

