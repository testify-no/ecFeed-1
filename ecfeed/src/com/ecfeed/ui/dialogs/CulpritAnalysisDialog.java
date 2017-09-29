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
	final private int fNumColumn = 5;
	final private int fMaxNumRow = 31;
	private int fRecordIndex;
	private int fTotal;
	final private int fComboMaxLimit;
	
	private DialogObjectToolkit fDialogObjectToolkit;
	private Table fTable = null;
	private Button fNextButton;
	private Button fPrevButton;
	private Combo fMinCombo;
	private Combo fMaxCombo;
	
	private TestResultsHolder fTestResultsHolder;
	private MethodNode fMethodNode;
	private List<TestResultDescription> fTestResultDescrs; 
	private TestResultsAnalysis fTestResultsAnalysis;
	private PagingContainer<Culprit> fPagingContainer;
	private List<Culprit> fCurrentPage;
	

	public CulpritAnalysisDialog(MethodNode methodNode, TestResultsHolder testResultsHolder) {
		
		super(null);
		
		fMethodNode = methodNode;
		fTestResultsHolder = testResultsHolder;
		fComboMaxLimit = methodNode.getMethodParameterCount();
		fTestResultDescrs = testResultsHolder.getTestResultDescription();
				
		fTestResultsAnalysis 
		= new TestResultsAnalyzer().generateAnalysis(fTestResultDescrs, n1, n2);
		fTotal = fTestResultsAnalysis.getCulpritCount();
		
		fPagingContainer = new PagingContainer<Culprit>(fMaxNumRow);
		fillUpPagingContainer();
		fCurrentPage = fPagingContainer.getCurrentPage();
		fRecordIndex = fPagingContainer.getFirstRecordIndex();

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
		fMinCombo = createCombo(ComboComposite, n1, new MinComboSelectionAdapter());
		createLabel(ComboComposite, "max");
		fMaxCombo = createCombo(ComboComposite, n2, new MaxComboSelectionAdapter());

		Composite TableComposite = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 1);
		fTable = createTableContents(TableComposite, fNumColumn);

		Composite ButtonComposite = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 2);
		fPrevButton = 
				fDialogObjectToolkit.createButton(
						ButtonComposite, "Previous Page", new PrevButtonSelectionAdapter());
		fNextButton = 
				fDialogObjectToolkit.createButton(
						ButtonComposite, "Next Page", new NextButtonSelectionAdapter());
		fPrevButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));
		fNextButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false));
		fPrevButton.setEnabled(false);
		
		return dialogArea;
	}
	
	private void fillUpPagingContainer() {
	
		List<Culprit> culpritList = fTestResultsAnalysis.getCulpritList();

		for (Culprit culprit: culpritList){
			fPagingContainer.addItem(culprit);	
		}
	}

	private Combo createCombo(Composite parentComposite, int defaultValue, SelectionListener selectionListener) {
		
		Combo c = fDialogObjectToolkit.createCombo(parentComposite, fComboMaxLimit, defaultValue);
		
		if (selectionListener != null){
			c.addSelectionListener(selectionListener);
		}
		return c;
	}

	private void createLabel(Composite parentComposite, String text) {
		
		fDialogObjectToolkit.createLabel(parentComposite, text);
	}

	private Table createTableContents(Composite parentComposite, int ColumnNr) {

		Table table = fDialogObjectToolkit.createTable(parentComposite);
		String[] ColumnNames = {"Rank", "Tuple", "Failure rate", "Occurences", "Fails"};
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

		for(Culprit culprit: fCurrentPage){
			createTableItem(table, culprit);
			fRecordIndex++;
		}

		table.setRedraw(true);	
	}

	private void createTableItem(Table table, Culprit culprit) {

		int c = 0;
		
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(c++, Integer.toString(fRecordIndex+1));
		item.setText(c++, culprit.getItem().toString());
		item.setText(c++, String.valueOf(culprit.getFailureIndex()));
		item.setText(c++, Integer.toString(culprit.getOccurenceCount()));
		item.setText(c++, Integer.toString(culprit.getFailureCount()));

	}
	
	public void refillTable() {
		
		fCurrentPage = fPagingContainer.getCurrentPage();
		fRecordIndex = fPagingContainer.getFirstRecordIndex();
		fTable.removeAll();
		fillTable(fTable);	
	}
	
	public void loadNewData() {
		
		if(n1 < n2){
			fTestResultsAnalysis = new TestResultsAnalyzer().generateAnalysis(fTestResultDescrs, n1, n2);
			fTotal = fTestResultsAnalysis.getCulpritCount();
			
			fPagingContainer.removeAllRecords();
			fillUpPagingContainer();
			refillTable();
		}	
	}

	private void makeColumnResizable(Composite parentComposite, TableColumn[] column) {
		
		parentComposite.addControlListener(new ColumnResizeListener(parentComposite, column));
	}
	
	class NextButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			
			if (fPagingContainer.hasNextPage()) {
				fPagingContainer.switchToNextPage();
				refillTable();				
			}

			if (fRecordIndex == fTotal && fNextButton.isEnabled()){
				fNextButton.setEnabled(false);
			}

			if (fRecordIndex > 0 && !fPrevButton.isEnabled()){
				fPrevButton.setEnabled(true);
			}
		}
	}

	class PrevButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			
			if (fPagingContainer.hasPreviousPage()){
				fPagingContainer.switchToPreviousPage();
				refillTable();
			}
			
			if (fRecordIndex < fTotal && !fNextButton.isEnabled()){
				fNextButton.setEnabled(true);
			}
			
			if (fRecordIndex == fMaxNumRow && fPrevButton.isEnabled()){
				fPrevButton.setEnabled(false);
			}
		}
	}
	
	class MinComboSelectionAdapter extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			n1 = Integer.parseInt(fMinCombo.getText());
			loadNewData();			
		}
	}
	
	class MaxComboSelectionAdapter extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			n2 = Integer.parseInt(fMaxCombo.getText());
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

		for (int i = 0; i < column.length; i++) {
			column[i].addListener(SWT.Selection, sortListener);
		}	
	}

	private class SortByHeadersListener implements Listener {

		Table fTable;

		public SortByHeadersListener(Table table) {
			
			fTable = table;
		}

		public void handleEvent(Event e) {
			
			TableColumn sortColumn = fTable.getSortColumn();
			TableColumn selectedColumn = (TableColumn) e.widget;
			int dir = fTable.getSortDirection();
			
			setSortDir(sortColumn, selectedColumn, dir);

			if (dir == SWT.UP) {
				fTestResultsAnalysis.SortColumnInput("SWT.UP", selectedColumn.getText());		
				fPagingContainer.removeAllRecords();
				fillUpPagingContainer();
				refillTable();		
			} else {
				fTestResultsAnalysis.SortColumnInput("SWT.DOWN", selectedColumn.getText());
				fPagingContainer.removeAllRecords();
				fillUpPagingContainer();
				refillTable();
			}			
		}

		private void setSortDir(TableColumn sortColumn, TableColumn selectedColumn, int dir) {

			if (sortColumn == selectedColumn) {
				if (dir == SWT.UP)
				{
					fTable.setSortDirection(SWT.DOWN);
				} else {
					fTable.setSortDirection(SWT.UP);
				}
			} else {
				fTable.setSortColumn(selectedColumn);
				dir = SWT.UP;
			}
		}
	}
}
