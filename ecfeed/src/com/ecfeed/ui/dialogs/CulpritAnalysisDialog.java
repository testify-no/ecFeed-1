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

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.TestCasesExportDialog.LoadButtonSelectionAdapter;
import com.ecfeed.ui.dialogs.TestCasesExportDialog.SaveAsButtonSelectionAdapter;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;

public class CulpritAnalysisDialog extends TitleAreaDialog{
	private DialogObjectToolkit fDialogObjectToolkit;

	public CulpritAnalysisDialog(Shell parent) {
		super(parent);
		fDialogObjectToolkit = DialogObjectToolkit.getInstance();
	}
	
	public CulpritAnalysisDialog(){
		super(null);
		fDialogObjectToolkit = DialogObjectToolkit.getInstance();
	}
	
	public void run()
	{
		setBlockOnOpen(true);
		
		open();
		
		Display.getCurrent().dispose();
	}
	
	@Override
	public void create()
	{
		super.create();
		setTitle("Test execution report");
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		Composite ChildComp = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 1);
		final String text = "Select tuple size for combinatoric analysis";
		createLabel(ChildComp, text);
		Composite ChildComp2 = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 4);
		createLabel(ChildComp2, "from");
		createCombo(ChildComp2);
		createLabel(ChildComp2, "to");
		createCombo(ChildComp2);
		Composite ChildComp3 = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 1);
		createTableContents(ChildComp3, 4);
		return dialogArea;
	}
	
	private void createCombo(Composite parentComposite)
	{
		fDialogObjectToolkit.createCombo(parentComposite, 10);
		
	}
	private void createLabel(Composite parentComposite, String text) {
		fDialogObjectToolkit.createLabel(parentComposite, text);
	}
	
	private void createTableContents(Composite parentComposite, int ColumnNr)
	{
		Table table = fDialogObjectToolkit.createTable(parentComposite);
		TableColumn[] column = new TableColumn[4];
		column[0] = new TableColumn(table, SWT.NONE);
		column[0].setText("Tuple");
		
		column[1] = new TableColumn(table, SWT.NONE);
		column[1].setText("Occurences");
		
		column[2] = new TableColumn(table, SWT.NONE);
		column[2].setText("Fails");
		
		column[3] = new TableColumn(table, SWT.NONE);
		column[3].setText("Index");
		
		fillTable(table, 10);
		
		for(int i = 0; i< column.length; i++)
		{
			column[i].pack();
		}	
	}
	
	private void fillTable(Table table, int rowNr) {
		table.setRedraw(false);
		for(int i = 0; i<rowNr; i++)
		{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText("-");
		}
		table.setRedraw(true);	
	}

	public static void main(String[] args)
	{
		new CulpritAnalysisDialog().run();
	}
	



	

}
