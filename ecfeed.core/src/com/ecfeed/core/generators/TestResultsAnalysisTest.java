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

public class TestResultsAnalysisTest {

	@Test
	public void shouldReturnCountZeroWhenEmpty() {
		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		assertEquals(testResultsAnalysis.getCulpritCount(), 0);
	}

	@Test
	public void shouldReturnAggregateOneCulprit() {

		Culprit culprit = createCulpritWith1Dimension("1", 1, 0);

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		testResultsAnalysis.aggregateCulprit(culprit);

		assertEquals(testResultsAnalysis.getCulpritCount(), 1);

		Culprit aggregatedCulprit = testResultsAnalysis.getCulprit(0);

		assertTrue(culprit.isBasicMatch(aggregatedCulprit));
	}	

	@Test
	public void shouldReturnAggregateCulpritTwiceVerA() {

		Culprit culprit = createCulpritWith1Dimension("1", 1, 0);

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		testResultsAnalysis.aggregateCulprit(culprit);
		testResultsAnalysis.aggregateCulprit(culprit);

		assertEquals(testResultsAnalysis.getCulpritCount(), 1);

		Culprit aggregatedCulprit = testResultsAnalysis.getCulprit(0);

		assertTrue(culprit.isTupleMatch(aggregatedCulprit));
		assertEquals(2, aggregatedCulprit.getOccurenceCount());
		assertEquals(0, aggregatedCulprit.getFailureCount());
	}	

	@Test
	public void shouldReturnAggregateCulpritTwiceVerB() {

		Culprit culprit = createCulpritWith1Dimension("1", 1, 1);

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		testResultsAnalysis.aggregateCulprit(culprit);
		testResultsAnalysis.aggregateCulprit(culprit);

		assertEquals(testResultsAnalysis.getCulpritCount(), 1);
		Culprit aggregatedCulprit = testResultsAnalysis.getCulprit(0);

		assertTrue(culprit.isTupleMatch(aggregatedCulprit));
		assertEquals(2, aggregatedCulprit.getOccurenceCount());
		assertEquals(2, aggregatedCulprit.getFailureCount());
	}	

	@Test
	public void shouldReturnAggregateTwoDifferentCulpritsOfSameSize() {

		Culprit culprit1 = createCulpritWith1Dimension("1", 1, 1);
		Culprit culprit2 = createCulpritWith1Dimension("2", 1, 1);

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		testResultsAnalysis.aggregateCulprit(culprit1);
		testResultsAnalysis.aggregateCulprit(culprit2);

		assertEquals(testResultsAnalysis.getCulpritCount(), 2);
		Culprit aggregatedCulprit1 = testResultsAnalysis.getCulprit(0);
		Culprit aggregatedCulprit2 = testResultsAnalysis.getCulprit(1);

		assertTrue(culprit1.isTupleMatch(aggregatedCulprit1));
		assertTrue(culprit2.isTupleMatch(aggregatedCulprit2));
	}	

	@Test
	public void shouldReturnAggregateTwoCulpritsOfDifferentSizes() {

		Culprit culprit1 = createCulpritWith1Dimension("1", 3, 1);
		Culprit culprit2 = createCulpritWith2Dimensions("1", "1", 5, 2);

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		testResultsAnalysis.aggregateCulprit(culprit1);
		testResultsAnalysis.aggregateCulprit(culprit2);

		assertEquals(testResultsAnalysis.getCulpritCount(), 2);

		Culprit[] arr = {culprit1, culprit2};
		assertTrue(analysisContainsCulprits(testResultsAnalysis, arr));
	}

	@Test
	public void shouldReturnAggregateMultipleCulprits() {

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();

		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("1", "1", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("5", "2", 1, 0));
		testResultsAnalysis.aggregateCulprit(createCulpritWith1Dimension("5", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith1Dimension("1", 1, 0));
		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("1", "1", 1, 0));
		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("5", "2", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("1", "1", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith1Dimension("5", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("5", "2", 1, 0));
		testResultsAnalysis.aggregateCulprit(createCulpritWith1Dimension("5", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith1Dimension("1", 1, 1));

		assertEquals(4, testResultsAnalysis.getCulpritCount());

		Culprit[] aggregatedCulprits = {
				createCulpritWith1Dimension("1", 2, 1),
				createCulpritWith2Dimensions("1", "1", 3, 2),
				createCulpritWith2Dimensions("5", "2", 3, 1),
				createCulpritWith1Dimension("5", 3, 3) };

		assertTrue(analysisContainsCulprits(testResultsAnalysis, aggregatedCulprits));
	}	

	private static Culprit createCulpritWith1Dimension(String testInputValue1, int occurences, int failures) {

		List<DimItem> testInput = new ArrayList<DimItem>();
		DimItem dimension1 = new DimItem(0, testInputValue1);
		testInput.add(dimension1);

		Culprit culprit = new Culprit(testInput, occurences, failures);
		return culprit;
	}

	private static Culprit createCulpritWith2Dimensions(String testInputValue1, String testInputValue2, int occurences, int failures) {

		List<DimItem> testInput = new ArrayList<DimItem>();

		DimItem dimension1 = new DimItem(0, testInputValue1);
		testInput.add(dimension1);

		DimItem dimension2 = new DimItem(0, testInputValue2);
		testInput.add(dimension2);

		Culprit culprit = new Culprit(testInput, occurences, failures);
		return culprit;
	}	

	private boolean analysisContainsCulprits(TestResultsAnalysis testResultsAnalysis, Culprit[] culprits) {

		for (Culprit culprit : culprits) {
			if (!testResultsAnalysis.containsCulprit(culprit)) {
				return false;
			}
		}

		return true;
	}

}
