package com.ecfeed.junit.onlinerunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ecfeed.core.generators.CartesianProductGenerator;
import com.ecfeed.junit.OnlineRunner;
import com.ecfeed.junit.annotations.EcModel;
import com.ecfeed.junit.annotations.Generator;

@RunWith(OnlineRunner.class)
@EcModel("test/com/ecfeed/junit/onlinerunner/OnlineRunnerTests.ect")
public class OnlineRunnerTest2 {

	@Before
	public void beforeTestMethod() {
		LauncherForOnlineRunnerTests.appendToLog("B ");
	}

	@Test
	@Generator(CartesianProductGenerator.class)
	public void testMethod1(int arg1, int arg2) {
		LauncherForOnlineRunnerTests.appendToLog("F(" + arg1 + "," + arg2 + ") ");
	}

	@After
	public void afterTestMethod() {
		LauncherForOnlineRunnerTests.appendToLog("A ");
	}

}

