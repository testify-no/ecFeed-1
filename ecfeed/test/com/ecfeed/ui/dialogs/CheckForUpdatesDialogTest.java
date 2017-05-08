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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ecfeed.algorithm.CurrentReleases;

public class CheckForUpdatesDialogTest{

	final static boolean AUTOMATICALLY_CHECK_FOR_UPDATES_FALSE = false;
	final static boolean AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE = true;

	final static boolean CHECK_BETA_VERSIONS_FALSE = false;
	final static boolean CHECK_BETA_VERSIONS_TRUE = true;


	@Test
	public void shouldNotOpenWhenCurrentReleasesAreNull() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.1",
						null,
						AUTOMATICALLY_CHECK_FOR_UPDATES_FALSE,
						CHECK_BETA_VERSIONS_FALSE,
						"0.0.0",
						"0.0.0");

		assertEquals(false, shouldOpen);
	}

	@Test
	public void shouldNotOpenWhenAutomaticallyCheckForUpdatesIsFalse() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.1",
						createCurrentReleases("", ""),
						AUTOMATICALLY_CHECK_FOR_UPDATES_FALSE,
						CHECK_BETA_VERSIONS_FALSE,
						"0.0.0",
						"0.0.0");

		assertEquals(false, shouldOpen);
	}

	@Test
	public void shouldNotOpenWhenThereIsNoCurrentVersion() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.1",
						createCurrentReleases("", ""),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_FALSE,
						"0.0.0",
						"0.0.0");

		assertEquals(false, shouldOpen);
	}

	@Test
	public void shouldNotOpenWhenCurrentReleaseIsSmaller() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.1",
						createCurrentReleases("1.10.1", ""),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_FALSE,
						"0.0.0",
						"0.0.0");

		assertEquals(false, shouldOpen);
	}	

	@Test
	public void shouldNotOpenWhenCurrentStandardVersionIsEqual() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.1",
						createCurrentReleases("1.11.1", ""),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_FALSE,
						"0.0.0",
						"0.0.0");

		assertEquals(false, shouldOpen);
	}	

	@Test
	public void shouldNotOpenWhenCurrentBetaVersionIsEmpty() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.1",
						createCurrentReleases("1.11.1", ""),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_TRUE,
						"0.0.0",
						"0.0.0");

		assertEquals(false, shouldOpen);
	}	

	@Test
	public void shouldNotOpenWhenCurrentBetaVersionIsSmaller() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.1",
						createCurrentReleases("1.11.1", "1.11.0"),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_TRUE,
						"0.0.0",
						"0.0.0");

		assertEquals(false, shouldOpen);
	}	

	@Test
	public void shouldNotOpenWhenCurrentBetaVersionIsGreaterButNoBetaVersionCheck() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.0",
						createCurrentReleases("1.10.1", "1.12.0"),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_FALSE,
						"0.0.0",
						"0.0.0");

		assertEquals(false, shouldOpen);
	}	

	@Test
	public void shouldOpenWhenCurrentStandardVersionIsGreater() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.1",
						createCurrentReleases("1.11.3", ""),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_FALSE,
						"0.0.0",
						"0.0.0");

		assertEquals(true, shouldOpen);
	}	

	@Test
	public void shouldNotOpenWhenCurrentBetaVersionIsGreater() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.1",
						createCurrentReleases("1.11.1", "1.11.2"),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_FALSE,
						"0.0.0",
						"0.0.0");

		assertEquals(false, shouldOpen);
	}	

	@Test
	public void shouldOpenWhenCurrentBetaVersionIsGreater() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.1",
						createCurrentReleases("1.11.1", "1.11.2"),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_TRUE,
						"0.0.0",
						"0.0.0");

		assertEquals(true, shouldOpen);
	}	

	@Test
	public void shouldNotOpenBecauseOfIgnoredStandardVersion() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.0",
						createCurrentReleases("1.11.2", "1.11.0"),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_TRUE,
						"1.11.2",
						"0.0.0");

		assertEquals(false, shouldOpen);
	}	

	@Test
	public void shouldOpenWhenIgnoredStandardVersionIsTooLow() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.0",
						createCurrentReleases("1.11.4", "1.11.0"),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_TRUE,
						"1.11.2",
						"0.0.0");

		assertEquals(true, shouldOpen);
	}	

	@Test
	public void shouldNotOpenBecauseOfIgnoredBetaVersion() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.0",
						createCurrentReleases("1.11.0", "1.11.2"),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_TRUE,
						"0.0.0",
						"1.11.2");

		assertEquals(false, shouldOpen);
	}

	@Test
	public void shouldOpenWhenIgnoredBetaVersionIsTooLow() {

		boolean shouldOpen = 
				CheckForUpdatesDialog.shouldOpenConditionalDialog(
						"1.11.0",
						createCurrentReleases("1.11.0", "1.11.4"),
						AUTOMATICALLY_CHECK_FOR_UPDATES_TRUE,
						CHECK_BETA_VERSIONS_TRUE,
						"0.0.0",
						"1.11.2");

		assertEquals(true, shouldOpen);
	}	


	private static CurrentReleases createCurrentReleases(String versionStandard, String versionBeta) {

		CurrentReleases currentReleases = new CurrentReleases();
		currentReleases.versionStandard = versionStandard;
		currentReleases.versionBeta = versionBeta;

		return currentReleases;
	}

}
