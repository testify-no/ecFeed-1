/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.ui.dialogs.basic;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.dialogs.CulpritAnalysisDialog;
import com.ecfeed.ui.modelif.TestResultsHolder;

import org.eclipse.jface.dialogs.ErrorDialog;


public class AdvancedStatisticsButtonDialog extends ErrorDialog {
	
	MethodNode fmethodNode;
	TestResultsHolder ftestResultsHolder;
	
	public AdvancedStatisticsButtonDialog(Shell parentShell, String title, String message, IStatus status, int displayMask, MethodNode methodnode, TestResultsHolder testResultsHolder){
		
		super(parentShell, title, message, status, displayMask);
		fmethodNode = methodnode;
		ftestResultsHolder = testResultsHolder;
		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		Button adst = createButton(parent, IDialogConstants.OPEN_ID, "advanced statistics", true);
		adst.addSelectionListener(new AdvancedStatistics(fmethodNode, ftestResultsHolder));
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createDetailsButton(parent);
	}
	
}

class AdvancedStatistics extends SelectionAdapter {
	
	MethodNode fmethodNode;
	TestResultsHolder ftestResultsHolder;

	public AdvancedStatistics(MethodNode methodNode, TestResultsHolder testResultsHolder)
	{
		fmethodNode = methodNode;
		ftestResultsHolder = testResultsHolder;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e)
	{
		new CulpritAnalysisDialog(fmethodNode, ftestResultsHolder);
	}
	
}