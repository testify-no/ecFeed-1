/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestResultsAnalyzerTest {

	@Test
	public void shouldReturnCountZeroWhenEmpty() {

		TestResultsAnalyzer testResultsAnalyzer = new TestResultsAnalyzer();

		List<TestResultDescription> testResults = new ArrayList<TestResultDescription>(); 

		TestResultsAnalysis testResultsAnalysis = 
				testResultsAnalyzer.generateAnalysis(testResults, 0, 0);

		assertEquals(testResultsAnalysis.getCulpritCount(), 0);
	}


	@Test
	public void shouldcheckGenerationForOneResultWithTrueResult() {

		testForOneResult(true, 0);
	}

	@Test
	public void shouldcheckGenerationForOneResultWithTrueFalse() {

		testForOneResult(false, 1);
	}	

	private void testForOneResult(boolean testResult, int expectedFailureCount) {

		List<TestResultDescription> testResultDescrs = new ArrayList<TestResultDescription>();
		addTestResult(new String[]{ "1" }, testResult, testResultDescrs);

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 1, 1);

		assertEquals(1, testResultsAnalysis.getCulpritCount());
		Culprit culprit = testResultsAnalysis.getCulprit(0);


		Culprit expectedCulprit = new Culprit(new DimItem[]{new DimItem(0, "1")}, 1, expectedFailureCount);

		assertTrue(expectedCulprit.isMatch(culprit));		
	}

	@Test
	public void shouldGenerateAnalysisForOneResultWithThreeArgs() {

		List<TestResultDescription> testResultDescrs = new ArrayList<TestResultDescription>();

		addTestResult(new String[]{ "1", "2", "3" }, true, testResultDescrs);

		checkGenerationForN1(testResultDescrs);
		checkGenerationForN2(testResultDescrs);
		checkGenerationForN3(testResultDescrs);

		checkGenerationForN1to2(testResultDescrs);
		checkGenerationForN2to3(testResultDescrs);

		checkGenerationForN1to3(testResultDescrs);
	}	

	private void checkGenerationForN1(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 1, 1);

		assertEquals(3, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN1(testResultsAnalysis);
	}

	private void checkGenerationForN2(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 2, 2);

		assertEquals(3, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN2(testResultsAnalysis);
	}

	private void checkGenerationForN3(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 3, 3);

		assertEquals(1, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN3(testResultsAnalysis);
	}

	private void checkGenerationForN1to2(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 1, 2);

		assertEquals(6, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN1(testResultsAnalysis);
		checkCulpritsForN2(testResultsAnalysis);
	}

	private void checkGenerationForN2to3(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 2, 3);

		assertEquals(4, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN2(testResultsAnalysis);
		checkCulpritsForN3(testResultsAnalysis);
	}	

	private void checkGenerationForN1to3(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 1, 3);

		assertEquals(7, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN1(testResultsAnalysis);
		checkCulpritsForN2(testResultsAnalysis);
		checkCulpritsForN3(testResultsAnalysis);
	}	

	private void checkCulpritsForN1(TestResultsAnalysis testResultsAnalysis) {

		Culprit culprit = new Culprit(new DimItem[]{ new DimItem(0, "1") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimItem[]{ new DimItem(1, "2") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimItem[]{ new DimItem(2, "3") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		
	}

	private void checkCulpritsForN2(TestResultsAnalysis testResultsAnalysis) {

		Culprit culprit = new Culprit(new DimItem[]{ new DimItem(0, "1"), new DimItem(1, "2") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimItem[]{ new DimItem(1, "2"), new DimItem(2, "3") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		

		culprit = new Culprit(new DimItem[]{ new DimItem(0, "1"), new DimItem(2, "3") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		
	}

	private void checkCulpritsForN3(TestResultsAnalysis testResultsAnalysis) {

		Culprit culprit = new Culprit(new DimItem[]{ new DimItem(0, "1"), new DimItem(1, "2"), new DimItem(2, "3") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));
	}

	@Test
	public void shouldGenerateAnalysisForThreeResultsWithTwoArgs() {

		List<TestResultDescription> testResultDescrs = new ArrayList<TestResultDescription>();

		addTestResult(new String[]{ "2", "1" }, true, testResultDescrs);
		addTestResult(new String[]{ "1", "2" }, false, testResultDescrs);
		addTestResult(new String[]{ "1", "3" }, false, testResultDescrs);

		checkGeneration2by3forN1(testResultDescrs);
		checkGeneration2by3forN2(testResultDescrs);
	}

	private void checkGeneration2by3forN1(List<TestResultDescription> testResultDescrs) {
		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 1, 1);

		assertEquals(5, testResultsAnalysis.getCulpritCount());

		Culprit culprit = new Culprit(new DimItem[]{ new DimItem(0, "2") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimItem[]{ new DimItem(1, "1") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		

		culprit = new Culprit(new DimItem[]{ new DimItem(0, "1") }, 2, 2);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimItem[]{ new DimItem(1, "2") }, 1, 1);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		

		culprit = new Culprit(new DimItem[]{ new DimItem(1, "2") }, 1, 1);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		
	}

	private void checkGeneration2by3forN2(List<TestResultDescription> testResultDescrs) {
		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 2, 2);

		assertEquals(3, testResultsAnalysis.getCulpritCount());

		Culprit culprit = new Culprit(new DimItem[]{ new DimItem(0, "2"), new DimItem(1, "1") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimItem[]{ new DimItem(0, "1"), new DimItem(1, "2") }, 1, 1);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimItem[]{ new DimItem(0, "1"), new DimItem(1, "3") }, 1, 1);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		
	}

	private void addTestResult(
			String[] testArguments, boolean result, List<TestResultDescription> testResultDescrs) {

		List<String> testArgList = new ArrayList<String>();

		for (String testArgument : testArguments) {
			testArgList.add(testArgument);
		}

		testResultDescrs.add(new TestResultDescription(testArgList, result));
	}

}
