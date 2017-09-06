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
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.ecfeed.core.generators.TestResultDescription;
import com.ecfeed.core.generators.TestResultsAnalysis;
import com.ecfeed.core.generators.TestResultsAnalyzer;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;

public class CulpritAnalysisDialog extends TitleAreaDialog {

	private DialogObjectToolkit fDialogObjectToolkit;
	private int count = 0;
	private int numRow = 0;
	private Table table = null;
	private List<TestResultDescription> testResultDescrs = createtestResultDescrs();
	private TestResultsAnalysis testResultsAnalysis = new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 2, 5);

	public CulpritAnalysisDialog(Shell parent) {
		super(parent);
		fDialogObjectToolkit = DialogObjectToolkit.getInstance();
	}

	public CulpritAnalysisDialog() {
		super(null);
		fDialogObjectToolkit = DialogObjectToolkit.getInstance();
	}

	public void run() {
		setBlockOnOpen(true);
		open();
		Display.getCurrent().dispose();
	}

	@Override
	public void create() {
		super.create();
		setTitle("Test execution report");	
	}

	@Override
	protected boolean isResizable()	{
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite dialogArea = (Composite) super.createDialogArea(parent);

		Composite LabelComposite = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 1);
		final String text = "Select tuple size for combinatoric analysis";
		createLabel(LabelComposite, text);

		Composite ComboComposite = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 4);
		createLabel(ComboComposite, "min");
		createCombo(ComboComposite, 0);
		createLabel(ComboComposite, "max");
		createCombo(ComboComposite, 2);

		Composite TableComposite = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 1);
		table = createTableContents(TableComposite, 4);

		Composite ButtonComposite = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 2);

		Button PrevButton = 
				fDialogObjectToolkit.createButton(
						ButtonComposite, "Previous Page", new PrevButtonSelectionAdapter());

		Button NextButton = 
				fDialogObjectToolkit.createButton(
						ButtonComposite, "Next Page", new NextButtonSelectionAdapter());

		PrevButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));
		NextButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false));

		return dialogArea;
	}

	private void createCombo(Composite parentComposite, int defaultValue) {
		fDialogObjectToolkit.createCombo(parentComposite, 10, defaultValue);	
	}

	private void createLabel(Composite parentComposite, String text) {
		fDialogObjectToolkit.createLabel(parentComposite, text);
	}

	private Table createTableContents(Composite parentComposite, int ColumnNr) {

		Table table = fDialogObjectToolkit.createTable(parentComposite);
		String[] ColumnNames = {"Rank", "Tuple", "Failure Index", "Occurences", "Fails"};
		TableColumn[] column = fDialogObjectToolkit.addColumn(table, 5, ColumnNames);
		fillTable(table, testResultDescrs);

		for (int i = 0; i< column.length; i++) {
			column[i].pack();
		}

		MakeColumnResizable(parentComposite, column);
		return table;
	}

	private void fillTable(Table table, List<TestResultDescription> testResultDescrs ) {

		table.setRedraw(false);

		if (numRow == 0) {
			if (testResultsAnalysis.getCulpritCount() > 31) {
				numRow = 31;
			} else {
				numRow = testResultsAnalysis.getCulpritCount();
			}
		}

		while(count < numRow) {
			testResultsAnalysis.calculateFailureIndexes();
			TableItem item = new TableItem(table, SWT.NONE);
			int c = 0;
			item.setText(c++, Integer.toString(count+1));
			item.setText(c++, testResultsAnalysis.getCulprit(count).getItem().toString());
			item.setText(c++, String.valueOf(testResultsAnalysis.getCulprit(count).getFailureIndex()));
			item.setText(c++, Integer.toString(testResultsAnalysis.getCulprit(count).getOccurenceCount()));
			item.setText(c++, Integer.toString(testResultsAnalysis.getCulprit(count).getFailureCount()));
			count++;
		}

		table.setRedraw(true);	
	}

	private void MakeColumnResizable(Composite parentComposite, TableColumn[] column) {
		parentComposite.addControlListener(new ColumnResizeListener(parentComposite, column));
	}

	private class ColumnResizeListener extends ControlAdapter {

		Composite fParentComposite;
		TableColumn[] fColumn;

		public ColumnResizeListener(Composite parentComposite, TableColumn[] column) {
			fParentComposite = parentComposite;
			fColumn = column;
		}

		@Override
		public void controlResized(ControlEvent e) {

			Rectangle area = fParentComposite.getClientArea();
			Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			int width = area.width - 2 * table.getBorderWidth();

			if (preferredSize.y > area.height + table.getHeaderHeight()) {
				Point vBarSize = table.getVerticalBar().getSize();
				width -= vBarSize.x;
			}

			Point oldSize = table.getSize();

			if (oldSize.x > area.width) {

				for (int i = 0; i < fColumn.length; i++) {
					if (i == 1) { //More space needed to describe the tuple
						fColumn[i].setWidth(width / 2);
					} else {
						fColumn[i].setWidth(((width - width / 3))/(fColumn.length));
					}
				}

				table.setSize(area.width, area.height);

			} else {

				table.setSize(area.width, area.height);

				for (int i = 0; i < fColumn.length; i++) {

					if (i == 1) { //More space needed to describe the tuple
						fColumn[i].setWidth(width / 2);
					} else {
						fColumn[i].setWidth((width - width / 3)/(fColumn.length));
					}
				}
			}
		}

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

	class NextButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			int total = testResultsAnalysis.getCulpritCount();

			if (total - numRow >= 31) {
				numRow = numRow + 31;
				table.removeAll();
				fillTable(table, testResultDescrs);
			} else if (numRow < total) {
				numRow = total;
				table.removeAll();
				fillTable(table, testResultDescrs);
			}
		}
	}

	class PrevButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent arg0) {

			if (numRow >= 31 && count >= 31*2) {
				numRow = numRow - 31;
				count = count - 31 * 2;
				table.removeAll();
				fillTable(table, testResultDescrs);
			} else if(count - 31 * 2 < 0) {
				count = 0;
				numRow = 31;
				table.removeAll();
				fillTable(table, testResultDescrs);
			}
		}
	}

	public static void main(String[] args) {
		new CulpritAnalysisDialog().run();
	}

}
