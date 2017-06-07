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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.ecfeed.net.HttpCommunicatorWithProgress;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;
import com.ecfeed.ui.dialogs.basic.ErrorDialog;

public class ChangeLogDialog {

	public static void getLogAndOpen() {

		String changeLogText = null;

		try {
			HttpCommunicatorWithProgress httpComunicatorWithProgress = new HttpCommunicatorWithProgress();

			final int timeoutInSeconds = 10;

			changeLogText = 
					httpComunicatorWithProgress.sendGetRequest(
							"https://raw.githubusercontent.com/ecfeed/ecFeed/master/ecfeed/doc/changelog.txt", 
							null,
							timeoutInSeconds);

		} catch(Exception e) {
			ErrorDialog.open("HTTP request failed", "Can not download change log. (Check your internet connection)");
			return;
		}

		ChangeLogDialogIntr changeLogDialogIntr = new ChangeLogDialogIntr(changeLogText);
		changeLogDialogIntr.open();
	}

	private static class ChangeLogDialogIntr extends TitleAreaDialog {

		private Button fOkButton;
		private String fChangeLogText;

		private ChangeLogDialogIntr(String changeLogText) {

			super(Display.getDefault().getActiveShell());
			setHelpAvailable(false);
			fChangeLogText = changeLogText;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			setTitle("Change log");
			Composite area = (Composite) super.createDialogArea(parent);

			Composite container = new Composite(area, SWT.NONE);
			GridLayout gridLayout = new GridLayout(1, false);
			gridLayout.marginLeft = 20;
			gridLayout.marginRight = 20;
			container.setLayout(gridLayout);

			GridData gridData = new GridData(GridData.FILL_BOTH);
			container.setLayoutData(gridData);

			DialogObjectToolkit.createGridText(container, 150, fChangeLogText);

			return area;
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
			return new Point(1000, 700);
		}

	}

}