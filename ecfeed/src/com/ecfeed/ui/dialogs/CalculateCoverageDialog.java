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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;

import com.ecfeed.core.adapter.IImplementationStatusResolver;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.ui.common.EclipseImplementationStatusResolver;
import com.ecfeed.ui.common.TestCasesViewerContentProvider;
import com.ecfeed.ui.common.TestCasesViewerLabelProvider;
import com.ecfeed.ui.common.TreeCheckStateListener;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;

public class CalculateCoverageDialog extends TitleAreaDialog {

	public static final String DIALOG_CALCULATE_COVERAGE_MESSAGE = "Select test cases to include in evaluation.";
	public static final String DIALOG_CALCULATE_COVERAGE_TITLE = "Calculate n-wise coverage";
	private static final int INITIAL_N_MAX = 5;

	private CoverageCalculator fCalculator;
	private MethodNode fMethod;
	private IJavaProjectProvider fJavaProjectProvider;
	private int fNMax;

	//Initial state of the tree viewer
	private final Object[] fInitChecked;
	private final Object[] fInitGrayed;
	private CheckboxTreeViewer fTestCasesViewer;
	private Table fCoverageTable;
	private CoverageTreeViewerListener fCheckStateListener;
	private IImplementationStatusResolver fStatusResolver;

	public CalculateCoverageDialog (
			Shell parentShell, 
			MethodNode method, 
			Object[] checked, 
			Object[] grayed,
			IJavaProjectProvider javaProjectProvider) throws InterruptedException {

		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);

		fJavaProjectProvider = javaProjectProvider;
		fMethod = method;

		fNMax = Math.min(fMethod.getParametersCount(), INITIAL_N_MAX); 

		try {
			fCalculator = new CoverageCalculator(fMethod.getMethodParameters(), fNMax);
		} catch (InterruptedException e) {
			this.close();
			throw e;
		}

		fStatusResolver = new EclipseImplementationStatusResolver(javaProjectProvider);
		fInitChecked = checked;
		fInitGrayed = grayed;
	}

	@Override
	public Point getInitialSize() {

		return new Point(600, 750);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		Button okButton = createButton(parent, IDialogConstants.OK_ID, DialogHelper.getOkLabel(), false);
		okButton.setEnabled(true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		setTitle(DIALOG_CALCULATE_COVERAGE_TITLE);
		setMessage(DIALOG_CALCULATE_COVERAGE_MESSAGE);

		Composite area = (Composite) super.createDialogArea(parent);

		Composite mainContainer = new Composite(area, SWT.NONE);
		mainContainer.setLayout(new GridLayout(1, false));
		mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createTestCaseComposite(mainContainer);
		createMaxNComposite(mainContainer);
		createCoverageTableWithMargins(mainContainer);

		selectTestCasesAndRecalculate(fTestCasesViewer, fInitChecked, fInitGrayed);

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				fillCoverageTableRows();
			}
		});

		return area;
	}

	private void selectTestCasesAndRecalculate(CheckboxTreeViewer viewer, Object[] checked, Object[] grayed) {

		viewer.setCheckedElements(checked);
		viewer.setGrayedElements(grayed);

		Set<TestCaseNode> testCases = new HashSet<>();

		for (Object element : checked) {
			//if the element is non grayed test suite name
			if (element instanceof String && Arrays.asList(grayed).contains(element) == false) {
				testCases.addAll(fMethod.getTestCases((String)element));
			} else if (element instanceof TestCaseNode) {
				testCases.add((TestCaseNode)element);
			}
		}

		fCheckStateListener.recalculateAndShowTestCases(testCases, true);
	}

	private void createTestCaseComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridData griddata = new GridData(SWT.FILL, SWT.FILL, true, true);
		griddata.minimumHeight = 250;
		composite.setLayoutData(griddata);

		Label selectTestCasesLabel = new Label(composite, SWT.WRAP);
		selectTestCasesLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		selectTestCasesLabel.setText(DIALOG_CALCULATE_COVERAGE_MESSAGE);

		createTestCaseViewer(composite);
	}

	private void createMaxNComposite(Composite parent) {

		Composite composite = DialogObjectToolkit.createGridComposite(parent, 2);

		DialogObjectToolkit.createLabel(composite, "N max ");

		final Spinner spinner = new Spinner(composite, SWT.BORDER | SWT.RIGHT);

		spinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		spinner.setValues(fNMax, 1, fMethod.getParametersCount(), 0, 1, 1);

		spinner.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				int newNMax = spinner.getSelection();

				try {
					fCalculator.initialize(newNMax);
				} catch (InterruptedException e1) {
					spinner.setSelection(fNMax);
					return;
				}

				fNMax = newNMax;
				selectTestCasesAndRecalculate(
						fTestCasesViewer, 
						fTestCasesViewer.getCheckedElements(), 
						fTestCasesViewer.getGrayedElements());
			}
		});
	}

	private void createTestCaseViewer(Composite parent) {

		Tree tree = new Tree(parent, SWT.CHECK | SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));

		fTestCasesViewer = new CheckboxTreeViewer(tree);
		fTestCasesViewer.setContentProvider(new TestCasesViewerContentProvider(fMethod));
		fTestCasesViewer.setLabelProvider(
				new TestCasesViewerLabelProvider(fStatusResolver, fMethod, fJavaProjectProvider));
		fTestCasesViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fTestCasesViewer.setInput(fMethod);

		fCheckStateListener = new CoverageTreeViewerListener(fTestCasesViewer);
		fTestCasesViewer.addCheckStateListener(fCheckStateListener);
	}

	private void createCoverageTableWithMargins(Composite parent) {

		Composite compositeAligningMargins = new Composite(parent, SWT.FILL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

		compositeAligningMargins.setLayout(new GridLayout(1, false));
		compositeAligningMargins.setLayoutData(gridData);

		createCoverageTable(compositeAligningMargins);
	}

	private void createCoverageTable(Composite parent) {

		Composite composite = new Composite(parent, SWT.FILL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumWidth = 100;
		gridData.minimumHeight = 100;

		composite.setLayout(new FillLayout());
		composite.setLayoutData(gridData);

		fCoverageTable = new Table(composite, SWT.BORDER);
		createColumns(fCoverageTable);

		initializeCoverageTableItems();
		fillCoverageTableRows();	    
	}

	private void initializeCoverageTableItems() {

		fCoverageTable.clearAll();

		for (int index = 0; index < fNMax; index++) {
			TableItem tableItem = new TableItem(fCoverageTable, SWT.NONE);
			tableItem.setText(new String[] { "", "" });
		}
	}

	private void fillCoverageTableRows() {

		double[] coverage = fCalculator.getCoverage();

		if (fNMax != coverage.length) {
			return;
		}

		for (int n = 0; n < fNMax; n++) {
			TableItem tableItem = fCoverageTable.getItem(n);
			fillTableItem(tableItem, n, coverage[n]);
		}
	}

	private void fillTableItem(TableItem tableItem, int n, double coverage) {

		tableItem.setText(0, new Integer(n+1).toString());
		tableItem.setText(1, String.format( "%.2f", coverage ));
	}


	private void createColumns(Table table) {
		TableColumn columnN = new TableColumn(table, SWT.CENTER);
		TableColumn columnCoverage = new TableColumn(table, SWT.CENTER);
		columnN.setText("N");
		columnCoverage.setText("Coverage");

		columnN.setWidth(280);
		columnCoverage.setWidth(280);

		table.setHeaderVisible(true);
	}

	private class CoverageTreeViewerListener extends TreeCheckStateListener {
		// saved tree state
		Object fTreeState[];

		public CoverageTreeViewerListener(CheckboxTreeViewer treeViewer) {

			super(treeViewer);
			fTreeState = new Object[0];
		}

		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {

			super.checkStateChanged(event);

			Object element = event.getElement();
			boolean checked = event.getChecked();
			Set<TestCaseNode> checkedTestCases;

			if(fTestCasesViewer.getCheckedElements().length == 0){
				checkedTestCases = null;
			} else {
				checkedTestCases = getCheckedTestCases(element, checked);
			}

			recalculateAndShowTestCases(checkedTestCases, checked);
		}

		public void recalculateAndShowTestCases(Collection<TestCaseNode> testCases, boolean checked) {

			fCalculator.setCurrentChangedCases(testCases, checked);

			if (fCalculator.calculateCoverage()) {
				fTreeState = getViewer().getCheckedElements();
				initializeCoverageTableItems();
				fillCoverageTableRows();
			} else {
				revertLastTreeChange();
			}
		}

		private Set<TestCaseNode> getCheckedTestCases(Object element, boolean checked) {

			Set<TestCaseNode> testCases = new HashSet<>();

			if (checked) {
				// TestSuite
				if (element instanceof String) {
					String testSuiteName = (String) element;
					testCases.addAll(fMethod.getTestCases(testSuiteName));
				}
				// TestCaseNode
				else {
					testCases.add((TestCaseNode) element);
				}
			}
			// if action is deselection
			else {
				// TestSuite
				if (element instanceof String) {
					String testSuiteName = (String) element;

					// if test suite was grayed add all test cases of that suite
					// that were checked
					for (Object tcase : fTreeState) {
						if (testSuiteName.equals(getContentProvider().getParent(tcase))) {
							testCases.add((TestCaseNode) tcase);
						}
					}
					// if parent has no children shown in the tree, but they all
					// are implicitly selected
					if (testCases.isEmpty()) {
						testCases.addAll(fMethod.getTestCases(testSuiteName));
					}
				}
				// TestCaseNode
				else {
					testCases.add((TestCaseNode) element);
				}
			}
			return testCases;
		}

		private void revertLastTreeChange() {

			getViewer().setCheckedElements(fTreeState);
		}

	}

}
