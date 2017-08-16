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

import java.util.ArrayList;
import java.util.List;

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

import com.ecfeed.core.generators.Culprit;
import com.ecfeed.core.generators.TestResultDescription;
import com.ecfeed.core.generators.TestResultsAnalysis;
import com.ecfeed.core.generators.TestResultsAnalyzer;
import com.ecfeed.core.generators.TestResultsAnalyzerTest;
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
		createCombo(ChildComp2, 0);
		createLabel(ChildComp2, "to");
		createCombo(ChildComp2, 2);
		Composite ChildComp3 = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 1);
		createTableContents(ChildComp3, 4);
		return dialogArea;
	}
	
	private void createCombo(Composite parentComposite, int defaultValue)
	{
		Combo combo = fDialogObjectToolkit.createCombo(parentComposite, 10, defaultValue);	
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
		
		List<TestResultDescription> testResultDescrs = createtestResultDescrs();
		System.out.println(testResultDescrs);
		fillTable(table, testResultDescrs);
		
		for(int i = 0; i< column.length; i++)
		{
			column[i].pack();
		}	
	}
	
	private void fillTable(Table table, List<TestResultDescription> testResultDescrs ) {
		table.setRedraw(false);
		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 0, 1);
		for(int i = 0; i<testResultsAnalysis.getCulpritCount(); i++)
		{
			TableItem item = new TableItem(table, SWT.NONE);
			int c = 0;
			item.setText(c++, "-");
			item.setText(c++, Integer.toString(testResultsAnalysis.getCulprit(i).getOccurenceCount()));
			item.setText(c++, Integer.toString(testResultsAnalysis.getCulprit(i).getFailureCount()));
			item.setText(c++, Integer.toString(testResultsAnalysis.getCulprit(i).getFailureIndex()));
		}
		table.setRedraw(true);	
	}
	
	private void addTestResult(
			String[] testArguments, boolean result, List<TestResultDescription> testResultDescrs) {

		List<String> testArgList = new ArrayList<String>();

		for (String testArgument : testArguments) {
			testArgList.add(testArgument);
		}

		testResultDescrs.add(new TestResultDescription(testArgList, result));
	}
	
	public List<TestResultDescription> createtestResultDescrs()
	{
		List<TestResultDescription> testResultDescrs = new ArrayList<TestResultDescription>();

		addTestResult(new String[]{ "1", "2", "3", "4", "5" }, false, testResultDescrs);
		addTestResult(new String[]{ "0", "2", "3", "5", "4" }, false, testResultDescrs);
		addTestResult(new String[]{ "5", "2", "3", "7", "8" }, true, testResultDescrs);
		addTestResult(new String[]{ "7", "7", "3", "9", "8" }, false, testResultDescrs);
		addTestResult(new String[]{ "2", "4", "5", "3", "8" }, true, testResultDescrs);
		return testResultDescrs;
	}

	public static void main(String[] args)
	{
		new CulpritAnalysisDialog().run();
	}
	



	

}
