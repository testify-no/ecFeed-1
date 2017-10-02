/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.generators.Culprit;
import com.ecfeed.core.generators.TestResultDescription;
import com.ecfeed.core.generators.TestResultsAnalysis;
import com.ecfeed.core.generators.TestResultsAnalyzer;

public class PagingContainerTest<RecordType> {

	@Test
	public void shouldAddItemsToPagingContainer(){

		List<TestResultDescription> testResultDescrs = createtestResultDescrs();
		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 0, 2);
		PagingContainer<Culprit> pagingContainer = new PagingContainer<Culprit>(5);
		List<Culprit> Culprits = testResultsAnalysis.getCulpritList();

		for(Culprit culprit: Culprits)
		{
			pagingContainer.addItem(culprit);
		}

		assertTrue(pagingContainer.getRecordsList().equals(Culprits));
		assertEquals(pagingContainer.getRecordsList(), Culprits);
	}

	@Test
	public void shouldHandleNullItems(){
		PagingContainer<String> pagingContainer = new PagingContainer<String>(3);
		List<String> list = new ArrayList<String>();

		for(String item:list)
		{
			pagingContainer.addItem(item);
		}

		pagingContainer.addItem(null);
		assertTrue(pagingContainer.getRecordsList().isEmpty());	
	}

	@Test
	public void shouldReturnCurrentPage(){
		List<TestResultDescription> testResultDescrs = createtestResultDescrs();
		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 0, 2);
		PagingContainer<Culprit> pagingContainer = new PagingContainer<Culprit>(5);
		List<Culprit> Culprits = testResultsAnalysis.getCulpritList();

		for(Culprit culprit: Culprits)
		{
			pagingContainer.addItem(culprit);
		}
		assertEquals(pagingContainer.getCurrentPage().size(), 5);

		List<Culprit> firstFiveCulprits = new ArrayList<Culprit>();
		for(int i = 0; i < 5; i++)
		{
			firstFiveCulprits.add(Culprits.get(i));
		}
		assertEquals(firstFiveCulprits, pagingContainer.getCurrentPage());

		pagingContainer.switchToNextPage();
		firstFiveCulprits.clear();
		for (int i=5; i < 10; i++)
		{
			firstFiveCulprits.add(Culprits.get(i));
		}
		assertEquals(firstFiveCulprits, pagingContainer.getCurrentPage());

		pagingContainer.switchToPreviousPage();
		firstFiveCulprits.clear();
		for (int i=0; i < 5; i++)
		{
			firstFiveCulprits.add(Culprits.get(i));
		}
		assertEquals(firstFiveCulprits, pagingContainer.getCurrentPage());	
	}

	@Test
	public void shouldRemoveAllItems()
	{
		List<TestResultDescription> testResultDescrs = createtestResultDescrs();
		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 0, 2);
		PagingContainer<Culprit> pagingContainer = new PagingContainer<Culprit>(5);
		List<Culprit> Culprits = testResultsAnalysis.getCulpritList();

		for (Culprit culprit: Culprits)
		{
			pagingContainer.addItem(culprit);
		}

		assertFalse(pagingContainer.getRecordsList().isEmpty());
		assertFalse(pagingContainer.getCurrentPage().isEmpty());

		pagingContainer.removeAllRecords();;
		assertTrue(pagingContainer.getRecordsList().isEmpty());
		assertTrue(pagingContainer.getCurrentPage().isEmpty());	
	}


	private void addTestResult(
			String[] testArguments, boolean result, List<TestResultDescription> testResultDescrs) {

		List<String> testArgList = new ArrayList<String>();

		for (String testArgument : testArguments) {
			testArgList.add(testArgument);
		}

		testResultDescrs.add(new TestResultDescription(testArgList, result));
	}

	public List<TestResultDescription> createtestResultDescrs()	{ 

		List<TestResultDescription> testResultDescrs = new ArrayList<TestResultDescription>();

		addTestResult(new String[]{ "1", "2", "3", "4", "5" }, false, testResultDescrs);
		addTestResult(new String[]{ "0", "2", "3", "5", "4" }, false, testResultDescrs);
		addTestResult(new String[]{ "5", "2", "3", "7", "8" }, true, testResultDescrs);
		addTestResult(new String[]{ "7", "7", "3", "9", "8" }, false, testResultDescrs);
		addTestResult(new String[]{ "2", "4", "5", "3", "8" }, true, testResultDescrs);

		return testResultDescrs;
	}
}
