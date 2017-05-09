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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.ecfeed.algorithm.CurrentReleases;
import com.ecfeed.algorithm.VersionCheckerAndRegistrator;
import com.ecfeed.application.ApplicationContext;
import com.ecfeed.application.ApplicationPreferences;
import com.ecfeed.application.ApplicationVersion;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.net.HttpCommunicatorWithProgress;
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

		HttpCommunicatorWithProgress httpCommunicatorWithProgress = new HttpCommunicatorWithProgress();

		CurrentReleases currentReleases = 
				VersionCheckerAndRegistrator.registerAndGetCurrentReleases(httpCommunicatorWithProgress);

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

		HttpCommunicatorWithProgress httpCommunicatorWithProgress = new HttpCommunicatorWithProgress();

		CurrentReleases currentReleases = 
				VersionCheckerAndRegistrator.registerAndGetCurrentReleases(httpCommunicatorWithProgress);

		if (!shouldOpenConditionalDialog(
				ApplicationContext.getEcFeedVersion(),
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


		if ( !(StringHelper.isNullOrEmpty(fCurrentReleases.versionStandard) &&
				StringHelper.isNullOrEmpty(fCurrentReleases.versionBeta))) {

			fCheckBoxDoNotRemind = 
					DialogObjectToolkit.createGridCheckBox(
							container, "Do not remind me about these versions.", new DoNotRemindAboutThisVersionValueApplier());
		}

		DialogObjectToolkit.createLabel(parent, " ");

		DialogObjectToolkit.createGridButton(container, "View changelog...", new ViewChangeLogSelectionListener());

		return area;
	}

	private String createTitle() {

		if (fCurrentReleases == null) {
			return "ecFeed server could not be contacted.";
		}

		if (isNewVersionAvailable()) {
			return "New version of ecFeed is available";
		}

		return "No new version found.";
	}

	private boolean isNewVersionAvailable() {

		if (fCurrentReleases == null) {
			return false;
		}

		String releaseVersion = fCurrentReleases.versionStandard;
		String softwareVersion = ApplicationContext.getEcFeedVersion();

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

		sb.append("You currently use ecFeed version " + ApplicationContext.getEcFeedVersion() + "\n");
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

		if (isNewVersionAvailable()) {
			sb.append(createDownloadInstructions());
		} else {
			sb.append("You use the latest ecFeed version. No need to update.");
		}


		return sb.toString();
	}

	private String createDownloadInstructions() {

		if (ApplicationContext.isStandaloneApplication()) {
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

		fOkButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		fOkButton.setEnabled(true);
	}

	@Override
	public void okPressed(){

		super.okPressed();
	}

	@Override
	protected Point getInitialSize() {

		return new Point(600, 450);
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
			String ignoreBetaVersionTo
			) {

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

		return ApplicationVersion.isThisNewerVersion(version, versionToCompare);
	}
}
