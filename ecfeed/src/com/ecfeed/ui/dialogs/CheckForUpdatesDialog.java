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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.ecfeed.algorithm.CurrentReleases;
import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;
import com.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit.GridButton;
import com.ecfeed.ui.editor.IValueApplier;

public class CheckForUpdatesDialog extends TitleAreaDialog {

	CurrentReleases fCurrentReleases;

	private Button fOkButton;

	public CheckForUpdatesDialog(CurrentReleases currentReleases) {
		super(Display.getDefault().getActiveShell());
		setHelpAvailable(false);

		fCurrentReleases = currentReleases;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(createTitle());
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		
//		Label lblSelectTestSuite = new Label(container, SWT.NONE);
//		lblSelectTestSuite.setText(createMainMessage());
		
		Label lblSelectTestSuite = DialogObjectToolkit.createLabel(parent, createMainMessage());
		
		
		Button checkBoxAutoCheck = DialogObjectToolkit.createGridCheckBox(parent, "Automatically check for updates on ecFeed start", null);
		Button checkBoxCheckBeta = DialogObjectToolkit.createGridCheckBox(parent, "Check beta versions", null);
		Button checkBoxDoNotRemind = DialogObjectToolkit.createGridCheckBox(parent, "Do not remind me about this version", null);
		
		DialogObjectToolkit.createLabel(parent, " ");
		GridButton buttonChangelog = DialogObjectToolkit.createGridButton(parent, "View changelog...", new ViewChangeLogSelectionListener());

		return area;
	}
	
	private String createTitle() {
		
		if (fCurrentReleases == null) {
			return "ecFeed server could not be contacted.";
		}
		
		if (isNewVersionAvailable()) {
			return "New version available";
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
		sb.append("The newest version available is " + fCurrentReleases.versionStandard + "\n");
		sb.append("The newest beta available is " + fCurrentReleases.versionBeta + "\n");
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
		return new Point(600, 400);
	}

	private class ViewChangeLogSelectionListener extends SelectionAdapter{

		@Override
		public void widgetSelected(SelectionEvent event){
		}
	}	
}
