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

import com.ecfeed.ui.dialogs.CulpritAnalysisDialog;

import org.eclipse.jface.dialogs.ErrorDialog;


public class AdvancedStatisticsButtonDialog extends ErrorDialog {
	
	public AdvancedStatisticsButtonDialog(Shell parentShell, String title, String message, IStatus status, int displayMask){
		
		super(parentShell, title, message, status, displayMask);
		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		Button adst = createButton(parent, IDialogConstants.OPEN_ID, "advanced statistics", true);
		adst.addSelectionListener(new AdvancedStatistics());
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createDetailsButton(parent);
	}
	
}

class AdvancedStatistics extends SelectionAdapter {
	
	@Override
	public void widgetSelected(SelectionEvent e)
	{
		new CulpritAnalysisDialog();
	}
	
}