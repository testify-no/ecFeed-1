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
import com.ecfeed.ui.common.utils.PagingContainer;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit.GridButton;
import com.ecfeed.ui.modelif.TestResultsHolder;


public class CulpritAnalysisDialog extends TitleAreaDialog {

	final private int fNumColumn = 5;
	final private int fMaxNumRow = 31;
	final private int fComboMaxLimit;

	private TestResultsAnalysis fTestResultsAnalysis;
	private List<TestResultDescription> fTestResultDescrs;
	private int fN1 = 0;
	private int fN2 = 2;

	private PagingContainer<Culprit> fPagingContainer;
	private List<Culprit> fCurrentPage;
	private int fRecordIndex;

	private Table fTable = null;
	private GridButton fNextButton;
	private GridButton fPrevButton;
	private Combo fMinCombo;
	private Combo fMaxCombo;

	private MethodNode fMethodNode; 
	private TestResultsHolder fTestResultsHolder;

	public CulpritAnalysisDialog(MethodNode methodNode, TestResultsHolder testResultsHolder) {
		super(null);

		fMethodNode = methodNode;
		fTestResultsHolder = testResultsHolder;
		fComboMaxLimit = fMethodNode.getMethodParameterCount();
		fTestResultDescrs = fTestResultsHolder.getTestResultDescription();

		fTestResultsAnalysis 
		= new TestResultsAnalyzer().generateAnalysis(fTestResultDescrs, fN1, fN2);

		fPagingContainer = new PagingContainer<Culprit>(fMaxNumRow);
		fillUpPagingContainer();
		fCurrentPage = fPagingContainer.getCurrentPage();
		fRecordIndex = fPagingContainer.getFirstRecordIndex();

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

		Composite labelComposite = (Composite) DialogObjectToolkit.createGridComposite(dialogArea, 1);
		final String text = "Select tuple size for combinatoric analysis";
		createLabel(labelComposite, text);

		Composite comboComposite = (Composite) DialogObjectToolkit.createGridComposite(dialogArea, 4);
		createCombos(comboComposite);

		Composite tableComposite = (Composite) DialogObjectToolkit.createGridComposite(dialogArea, 1);
		fTable = createTableContents(tableComposite, fNumColumn);

		Composite buttonComposite = (Composite) DialogObjectToolkit.createGridComposite(dialogArea, 2);
		createButtons(buttonComposite);

		return dialogArea;
	}

	public void refillTable() {

		fCurrentPage = fPagingContainer.getCurrentPage();
		fRecordIndex = fPagingContainer.getFirstRecordIndex();
		fTable.removeAll();
		fillTable(fTable);	
	}

	public void loadNewData() {

		if (fN1 > fN2) {
			return;
		}
		fTestResultsAnalysis = new TestResultsAnalyzer().generateAnalysis(fTestResultDescrs, fN1, fN2);

		fPagingContainer.removeAllRecords();
		fPagingContainer.setCurrentPageIndex(0);
		fillUpPagingContainer();
		refillTable();
		refreshPrevNextButtons();
	}

	private void fillUpPagingContainer() {

		List<Culprit> culpritList = fTestResultsAnalysis.getCulpritList();

		for (Culprit culprit: culpritList) {
			fPagingContainer.addItem(culprit);	
		}
	}

	public void refreshPrevNextButtons() {
		fPrevButton.setEnabled(fPagingContainer.hasPreviousPage());
		fNextButton.setEnabled(fPagingContainer.hasNextPage());		
	}

	private void createButtons(Composite ButtonComposite) {

		fPrevButton = 
				DialogObjectToolkit.createGridButton(
						ButtonComposite, "Previous Page", new PrevButtonSelectionAdapter());
		fNextButton = 
				DialogObjectToolkit.createGridButton(
						ButtonComposite, "Next Page", new NextButtonSelectionAdapter());
		fPrevButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));
		fNextButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false));

		refreshPrevNextButtons();
	}

	private void createCombos(Composite comboComposite) {

		createLabel(comboComposite, "min");
		fMinCombo = createCombo(comboComposite, fN1, new MinComboSelectionAdapter());
		fMinCombo.select(fN1);
		createLabel(comboComposite, "max");
		fMaxCombo = createCombo(comboComposite, fN2, new MaxComboSelectionAdapter());
		fMaxCombo.setText(Integer.toString(fN2));
	}

	private Combo createCombo(Composite parentComposite, int defaultValue, SelectionListener selectionListener) {

		Combo c = DialogObjectToolkit.createCombo(parentComposite, fComboMaxLimit, defaultValue);

		for (int i = 1; i <= fComboMaxLimit; i++) {
			c.add(Integer.toString(i));
		}

		if (selectionListener != null) {
			c.addSelectionListener(selectionListener);
		}
		return c;
	}

	private void createLabel(Composite parentComposite, String text) {

		DialogObjectToolkit.createLabel(parentComposite, text);
	}

	private Table createTableContents(Composite parentComposite, int ColumnNr) {

		Table table = DialogObjectToolkit.createTable(parentComposite);
		String[] ColumnNames = {"No.", "Tuple", "Failure rate", "Occurences", "Fails"};
		TableColumn[] column = DialogObjectToolkit.addColumn(table, fNumColumn, ColumnNames);
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

		for(Culprit culprit: fCurrentPage) {
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

	private void makeColumnResizable(Composite parentComposite, TableColumn[] column) {

		parentComposite.addControlListener(new ColumnResizeListener(parentComposite, column));
	}

	private void sortByHeaders(Table table, TableColumn[] column) {

		Listener sortListener = new SortByHeadersListener(table);

		for (int i = 0; i < column.length; i++) {
			column[i].addListener(SWT.Selection, sortListener);
		}	
	}

	private class NextButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent arg0) {

			if (!fPagingContainer.hasNextPage()) {
				return;
			} 
			fPagingContainer.switchToNextPage();
			refillTable();	
			refreshPrevNextButtons();
		}
	}

	private class PrevButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent arg0) {

			if (!fPagingContainer.hasPreviousPage()) {
				return;
			}

			fPagingContainer.switchToPreviousPage();
			refillTable();
			refreshPrevNextButtons();
		}
	}
	private class MinComboSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {

			fN1 = Integer.parseInt(fMinCombo.getText());
			loadNewData();			
		}
	}

	private class MaxComboSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {

			fN2 = Integer.parseInt(fMaxCombo.getText());
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
				fPagingContainer.setCurrentPageIndex(0);
				fillUpPagingContainer();
				refillTable();		
			} else {
				fTestResultsAnalysis.SortColumnInput("SWT.DOWN", selectedColumn.getText());
				fPagingContainer.removeAllRecords();
				fPagingContainer.setCurrentPageIndex(0);
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
