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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.ecfeed.algorithm.CurrentReleases;
import com.ecfeed.algorithm.VersionCheckerAndRegistrator;
import com.ecfeed.application.ApplicationPreferences;
import com.ecfeed.application.ApplicationVersionHelper;
import com.ecfeed.core.net.IHttpCommunicator;
import com.ecfeed.core.utils.ApplicationContext;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.net.HttpCommunicatorWithProgress;
import com.ecfeed.net.HttpCommunicatorWithoutProgress;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;
import com.ecfeed.ui.editor.IValueApplier;

public class CheckForUpdatesDialog extends TitleAreaDialog {

	static boolean fDialogWasOpen = false;

	CurrentReleases fCurrentReleases;

	Button fCheckBoxAutoCheck;
	Button fCheckBoxCheckBeta;
	Button fCheckBoxDoNotRemind;

	private Button fOkButton;

	public static void openUnconditionally() {

		IHttpCommunicator httpCommunicator = new HttpCommunicatorWithProgress();

		final int timeoutInSeconds = 15;

		CurrentReleases currentReleases = 
				VersionCheckerAndRegistrator.registerAppAndGetCurrentReleases(httpCommunicator, timeoutInSeconds);

		CheckForUpdatesDialog updatesDialog = new CheckForUpdatesDialog(currentReleases);
		updatesDialog.open();		
	}

	public static void openConditionally() {

		if (fDialogWasOpen) {
			return;
		}

		if (!ApplicationPreferences.getPreferenceAutomaticallyCheckForUpdates()) {
			return;
		}

		IHttpCommunicator httpCommunicator = new HttpCommunicatorWithoutProgress();

		final int timeoutInSeconds = 5;

		CurrentReleases currentReleases = 
				VersionCheckerAndRegistrator.registerAppAndGetCurrentReleases(httpCommunicator, timeoutInSeconds);

		if (!shouldOpenConditionalDialog(
				ApplicationVersionHelper.getEcFeedVersion(),
				currentReleases,
				ApplicationPreferences.getPreferenceAutomaticallyCheckForUpdates(),
				ApplicationPreferences.getPreferenceCheckBetaVersions(),
				ApplicationPreferences.getPreferenceIgnoreStandardVersionTo(),
				ApplicationPreferences.getPreferenceIgnoreBetaVersionTo())) {
			return;
		}

		CheckForUpdatesDialog updatesDialog = new CheckForUpdatesDialog(currentReleases);
		updatesDialog.open();	
		fDialogWasOpen = true;
	}

	private CheckForUpdatesDialog(CurrentReleases currentReleases) {
		super(Display.getDefault().getActiveShell());
		setHelpAvailable(false);

		fCurrentReleases = currentReleases;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(createTitle());
		Composite area = (Composite) super.createDialogArea(parent);

		Composite container = new Composite(area, SWT.NONE);

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginLeft = 20;
		gridLayout.marginRight = 20;
		container.setLayout(gridLayout);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(gridData);


		Label lblSelectTestSuite = new Label(container, SWT.NONE);
		lblSelectTestSuite.setText(createMainMessage());

		fCheckBoxAutoCheck = 
				DialogObjectToolkit.createGridCheckBox(
						container, "Automatically check for updates on ecFeed start", new AutoCheckValueApplier());

		boolean autoChecked = ApplicationPreferences.getPreferenceAutomaticallyCheckForUpdates(); 
		fCheckBoxAutoCheck.setSelection(autoChecked);

		fCheckBoxCheckBeta = 
				DialogObjectToolkit.createGridCheckBox(
						container, "Check beta versions on ecFeed start", new CheckBetaVersionsValueApplier());

		boolean checkBeta = ApplicationPreferences.getPreferenceCheckBetaVersions();
		fCheckBoxCheckBeta.setSelection(checkBeta);


		if ( fCurrentReleases != null &&
				!( StringHelper.isNullOrEmpty(fCurrentReleases.versionStandard) &&
						StringHelper.isNullOrEmpty(fCurrentReleases.versionBeta))) {

			fCheckBoxDoNotRemind = 
					DialogObjectToolkit.createGridCheckBox(
							container, "Do not remind me about these versions on ecFeed start", new DoNotRemindAboutThisVersionValueApplier());
		}

		DialogObjectToolkit.createLabel(parent, " ");

		DialogObjectToolkit.createGridButton(container, "View changelog...", new ViewChangeLogSelectionListener());

		return area;
	}

	private String createTitle() {

		if (fCurrentReleases == null) {
			return "ecFeed server could not be contacted.";
		}

		if (isNewStandardVersionAvailable()) {
			return "New version of ecFeed is available";
		}

		if (isNewBetaVersionAvailable()) {
			return "New beta version of ecFeed is available";
		}		

		return "No new version found.";
	}

	private boolean isAnyNewVersionAvailable() {

		if (isNewStandardVersionAvailable()) {
			return true;
		}

		if (isNewBetaVersionAvailable()) {
			return true;
		}		

		return false;
	}

	private boolean isNewStandardVersionAvailable() {

		if (isNewVersionAvailable(fCurrentReleases.versionStandard)) {
			return true;
		}

		return false;		
	}

	private boolean isNewBetaVersionAvailable() {

		if (isNewVersionAvailable(fCurrentReleases.versionBeta)) {
			return true;
		}

		return false;
	}

	private boolean isNewVersionAvailable(String releaseVersion) {

		if (fCurrentReleases == null) {
			return false;
		}

		String softwareVersion = ApplicationVersionHelper.getEcFeedVersion();

		int result = softwareVersion.compareTo(releaseVersion);

		if (result > 0) {
			return false;
		}

		return true;
	}	

	private String createMainMessage() {

		if (fCurrentReleases == null) {
			return "Check for updates failed.\nPlease try again later\n";
		}

		return createMainMessageWhenCheckSucceeded(); 
	}

	private String createMainMessageWhenCheckSucceeded() {

		StringBuilder sb = new StringBuilder();

		sb.append("You currently use ecFeed version " + ApplicationVersionHelper.getEcFeedVersion() + "\n");
		sb.append("\n");

		if (StringHelper.isNullOrEmpty(fCurrentReleases.versionStandard)) {
			sb.append("The newest version is unavailable\n");
		} else {
			sb.append("The newest version available is " + fCurrentReleases.versionStandard + "\n");
		}

		if (StringHelper.isNullOrEmpty(fCurrentReleases.versionBeta)) {
			sb.append("The newest beta version is unavailable\n");
		} else {
			sb.append("The newest beta available is " + fCurrentReleases.versionBeta + "\n");
		}

		sb.append("\n");

		if (isAnyNewVersionAvailable()) {
			sb.append(createDownloadInstructions());
		} else {
			sb.append("You use the latest ecFeed version. No need to update.");
		}

		return sb.toString();
	}

	private String createDownloadInstructions() {

		if (ApplicationContext.isApplicationTypeLocalStandalone()) {
			return "You can download the new version from:\nhttp://ecfeed.com/index.php/download/\n";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Go to Help -> Check for updates to update the plugin.\n");
		sb.append("\n");
		sb.append("Use the following update sites:\n");
		sb.append("http://ecfeed.com/repo/eclipse for full releases  and\n");
		sb.append("http://ecfeed.com/repo/eclipse.beta for beta releases.\n");

		return sb.toString();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		fOkButton = createButton(parent, IDialogConstants.OK_ID, DialogHelper.getOkLabel(), true);
		fOkButton.setEnabled(true);
	}

	@Override
	public void okPressed(){

		super.okPressed();
	}

	private class ViewChangeLogSelectionListener extends SelectionAdapter{

		@Override
		public void widgetSelected(SelectionEvent event){

			ChangeLogDialog.getLogAndOpen();
		}
	}

	private class AutoCheckValueApplier implements IValueApplier {

		@Override
		public void applyValue() {

			boolean isChecked = fCheckBoxAutoCheck.getSelection();
			ApplicationPreferences.setPreferenceAutomaticallyCheckForUpdates(isChecked);
		}

	}

	private class CheckBetaVersionsValueApplier implements IValueApplier {

		@Override
		public void applyValue() {

			boolean isChecked = fCheckBoxCheckBeta.getSelection();
			ApplicationPreferences.setPreferenceCheckBetaVersions(isChecked);
		}

	}	

	private class DoNotRemindAboutThisVersionValueApplier implements IValueApplier {

		@Override
		public void applyValue() {

			boolean isChecked = fCheckBoxDoNotRemind.getSelection();

			if (isChecked) {
				ApplicationPreferences.setPreferenceIgnoreStandardVersionTo(fCurrentReleases.versionStandard);
				ApplicationPreferences.setPreferenceIgnoreBetaVersionTo(fCurrentReleases.versionBeta);
			} else {
				ApplicationPreferences.setPreferenceIgnoreStandardVersionToInitial();
				ApplicationPreferences.setPreferenceIgnoreBetaVersionToInitial();
			}
		}

	}

	public static boolean shouldOpenConditionalDialog(
			String currentEcFeedVersion,
			CurrentReleases currentReleases,
			boolean automaticallyCheckForUpdates,
			boolean checkBetaVersions,
			String ignoreStandardVersionTo,
			String ignoreBetaVersionTo) {

		if (currentReleases == null) {
			return false;
		}

		if (!automaticallyCheckForUpdates) {
			return false;
		}

		if (StringHelper.isNullOrEmpty(currentReleases.versionStandard) &&
				StringHelper.isNullOrEmpty(currentReleases.versionBeta)) {
			return false;
		}
		if (isThisNewerVersion(currentReleases.versionStandard, currentEcFeedVersion ) &&
				isThisNewerVersion(currentReleases.versionStandard, ignoreStandardVersionTo)) {
			return true;
		}

		if (checkBetaVersions &&
				isThisNewerVersion(currentReleases.versionBeta, currentEcFeedVersion) &&
				isThisNewerVersion(currentReleases.versionBeta, ignoreBetaVersionTo)) {
			return true;
		}

		return false;
	}

	private static boolean isThisNewerVersion(String version, String versionToCompare) {

		return ApplicationVersionHelper.isThisNewerVersion(version, versionToCompare);
	}
}
