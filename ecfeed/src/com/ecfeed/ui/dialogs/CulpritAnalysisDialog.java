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

import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.ecfeed.core.generators.Culprit;
import com.ecfeed.core.generators.TestResultDescription;
import com.ecfeed.core.generators.TestResultsAnalysis;
import com.ecfeed.core.generators.TestResultsAnalyzer;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;
import com.ecfeed.ui.modelif.TestResultsHolder;


public class CulpritAnalysisDialog extends TitleAreaDialog {

	private int n1 = 0;
	private int n2 = 2;
	final private int fNumColumn = 4;
	final private int maxNumRow = 31;
	
	private int fCount;
	private int total;
	final private int ComboMaxLimit;
	
	private DialogObjectToolkit fDialogObjectToolkit;
	private Table fTable = null;
	private Button NextButton;
	private Button PrevButton;
	private Combo minCombo;
	private Combo maxCombo;
	
	private TestResultsHolder ftestResultsHolder;
	private MethodNode fmethodNode;
	private List<TestResultDescription> fTestResultDescrs; 
	private TestResultsAnalysis fTestResultsAnalysis;
	private PagingContainer<Culprit> pagingContainer;
	private List<Culprit> currentPage;
	

	public CulpritAnalysisDialog(MethodNode methodNode, TestResultsHolder testResultsHolder) {
		
		super(null);
		
		ftestResultsHolder = testResultsHolder;
		fTestResultDescrs = testResultsHolder.getTestResultDescription();
		
		fTestResultsAnalysis 
		= new TestResultsAnalyzer().generateAnalysis(fTestResultDescrs, n1, n2);
		total = fTestResultsAnalysis.getCulpritCount();
		
		fmethodNode = methodNode;
		ComboMaxLimit = methodNode.getMethodParameterCount();
		
		pagingContainer = new PagingContainer<Culprit>(maxNumRow);
		fillUpPagingContainer();
		currentPage = pagingContainer.getCurrentPage();
		fCount = pagingContainer.initialPageCount();

		fDialogObjectToolkit = DialogObjectToolkit.getInstance();
		setBlockOnOpen(true);
		open();
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
		minCombo = createCombo(ComboComposite, n1, new MinComboSelectionAdapter());
		createLabel(ComboComposite, "max");
		maxCombo = createCombo(ComboComposite, n2, new MaxComboSelectionAdapter());

		Composite TableComposite = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 1);
		fTable = createTableContents(TableComposite, fNumColumn);

		Composite ButtonComposite = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 2);
		PrevButton = 
				fDialogObjectToolkit.createButton(
						ButtonComposite, "Previous Page", new PrevButtonSelectionAdapter());
		NextButton = 
				fDialogObjectToolkit.createButton(
						ButtonComposite, "Next Page", new NextButtonSelectionAdapter());
		PrevButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));
		NextButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false));
		PrevButton.setEnabled(false);
		
		return dialogArea;
	}
	
	private void fillUpPagingContainer() {
	
		List<Culprit> culpritList = fTestResultsAnalysis.getCulpritList();

		for (Culprit culprit: culpritList)
		{
			pagingContainer.addItem(culprit);	
		}
	}

	private Combo createCombo(Composite parentComposite, int defaultValue, SelectionListener selectionListener) {
		Combo c = fDialogObjectToolkit.createCombo(parentComposite, ComboMaxLimit, defaultValue);
		
		if(selectionListener != null)
		{
			c.addSelectionListener(selectionListener);
		}
		return c;
	}

	private void createLabel(Composite parentComposite, String text) {
		fDialogObjectToolkit.createLabel(parentComposite, text);
	}

	private Table createTableContents(Composite parentComposite, int ColumnNr) {

		Table table = fDialogObjectToolkit.createTable(parentComposite);
		String[] ColumnNames = {"Rank", "Tuple", "Failure Index", "Occurences", "Fails"};
		TableColumn[] column = fDialogObjectToolkit.addColumn(table, fNumColumn, ColumnNames);
		fillTable(table);

		for (int i = 0; i< column.length; i++) {
			column[i].pack();
		}

		makeColumnResizable(parentComposite, column);
		sortByHeaders(table, column);
		return table;
	}

	private void fillTable(Table table) {

		table.setRedraw(false);

		for(Culprit culprit: currentPage)
		{
			createTableItem(table, culprit);
			fCount++;
		}

		table.setRedraw(true);	
	}

	private void createTableItem(Table table, Culprit culprit) {

		int c = 0;
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(c++, Integer.toString(fCount+1));
		item.setText(c++, culprit.getItem().toString());
		item.setText(c++, String.valueOf(culprit.getFailureIndex()));
		item.setText(c++, Integer.toString(culprit.getOccurenceCount()));
		item.setText(c++, Integer.toString(culprit.getFailureCount()));

	}
	
	public void refillTable() {
		currentPage = pagingContainer.getCurrentPage();
		fCount = pagingContainer.initialPageCount();
		fTable.removeAll();
		fillTable(fTable);	
	}
	
	public void loadNewData() {
		if(n1 < n2)
		{
			fTestResultsAnalysis = new TestResultsAnalyzer().generateAnalysis(fTestResultDescrs, n1, n2);
			total = fTestResultsAnalysis.getCulpritCount();
			
			pagingContainer.removeAll();
			fillUpPagingContainer();
			refillTable();
		}
		
	}

	private void makeColumnResizable(Composite parentComposite, TableColumn[] column) {
		parentComposite.addControlListener(new ColumnResizeListener(parentComposite, column));
	}
	
	class NextButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			if(pagingContainer.hasNextPage())
			{
				pagingContainer.switchToNextPage();
				refillTable();				
			}

			if (fCount == total && NextButton.isEnabled())
			{
				NextButton.setEnabled(false);
			}

			if (fCount > 0 && !PrevButton.isEnabled())
			{
				PrevButton.setEnabled(true);
			}
		}
	}

	class PrevButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			
			if(pagingContainer.hasPreviousPage())
			{
				pagingContainer.switchToPreviousPage();
				refillTable();
			}

			if (fCount < total && !NextButton.isEnabled())
			{
				NextButton.setEnabled(true);
			}
			if (fCount == maxNumRow && PrevButton.isEnabled())
			{
				PrevButton.setEnabled(false);
			}
		}
	}
	
	class MinComboSelectionAdapter extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			n1 = Integer.parseInt(minCombo.getText());
			loadNewData();			
		}
	}
	
	class MaxComboSelectionAdapter extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			n2 = Integer.parseInt(maxCombo.getText());
			loadNewData();		
		}
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
			Point preferredSize = fTable.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			int width = area.width - 2 * fTable.getBorderWidth();

			if (preferredSize.y > area.height + fTable.getHeaderHeight()) {
				Point vBarSize = fTable.getVerticalBar().getSize();
				width -= vBarSize.x;
			}

			Point oldSize = fTable.getSize();

			RegulateTableSize(oldSize, width, area);

		}

		private void RegulateTableSize(Point oldSize, int width, Rectangle area) {

			if (oldSize.x > area.width) {

				setColumnWidth(width);

				fTable.setSize(area.width, area.height);

			} else {

				fTable.setSize(area.width, area.height);

				setColumnWidth(width);
			}
		}

		private void setColumnWidth(int width) {

			for (int i = 0; i < fColumn.length; i++) {

				if (i == 1) { //More space needed to describe the tuple
					fColumn[i].setWidth(width / 2);
				} else {
					fColumn[i].setWidth(((width - width / 3))/(fColumn.length));
				}
			}
		}
	}


	private void sortByHeaders(Table table, TableColumn[] column)
	{

		Listener sortListener = new SortByHeadersListener(table);

		for (int i = 0; i < column.length; i++)
		{
			column[i].addListener(SWT.Selection, sortListener);
		}	
	}

	private class SortByHeadersListener implements Listener {

		Table fTable;

		public SortByHeadersListener(Table table) {
			fTable = table;
		}

		public void handleEvent(Event e)
		{
			TableColumn sortColumn = fTable.getSortColumn();
			TableColumn selectedColumn = (TableColumn) e.widget;
			int dir = fTable.getSortDirection();

			setSortDir(sortColumn, selectedColumn, dir);

			if(dir == SWT.UP)
			{
				fTestResultsAnalysis.SortColumnInput("SWT.UP", selectedColumn.getText());		
				pagingContainer.removeAll();
				fillUpPagingContainer();
				refillTable();
				
			} else 
			{
				fTestResultsAnalysis.SortColumnInput("SWT.DOWN", selectedColumn.getText());
				pagingContainer.removeAll();
				fillUpPagingContainer();
				refillTable();
			}			
		}

		private void setSortDir(TableColumn sortColumn, TableColumn selectedColumn, int dir) {

			if (sortColumn == selectedColumn)
			{
				if (dir == SWT.UP)
				{
					fTable.setSortDirection(SWT.DOWN);
				} else 
				{
					fTable.setSortDirection(SWT.UP);
				}
			} else 
			{
				fTable.setSortColumn(selectedColumn);
				dir = SWT.UP;
			}

		}

	}

}
