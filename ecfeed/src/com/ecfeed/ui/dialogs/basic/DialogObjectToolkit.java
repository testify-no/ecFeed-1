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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.utils.IValueApplier;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.ui.common.ApplyValueMode;
import com.ecfeed.ui.common.CommonEditHelper;
import com.ecfeed.utils.EclipseHelper;

public class DialogObjectToolkit {

	public static Composite createGridComposite(Composite parent, int countOfColumns) {

		Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayout(new GridLayout(countOfColumns, false));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);

		return composite;
	}

	public static Text createGridText(
			Composite parentGridComposite, int heightHint, String initialText) {

		Text templateText = new Text(parentGridComposite, SWT.MULTI
				| SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = heightHint;
		templateText.setLayoutData(gridData);

		if (initialText != null) {
			templateText.setText(initialText);
		}

		return templateText;
	}

	public static Text createTooltipText(Composite parentGridComposite, String tooltipText) {

		int textStyle = SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL;

		Text textWidget = new Text(parentGridComposite, textStyle); 

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		textWidget.setLayoutData(gridData);

		if (tooltipText != null) {
			textWidget.setText(tooltipText);
		}

		return textWidget;
	}


	public static Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);

		return label;
	}

	public static Label createSpacer(Composite parent, int size) {

		return createLabel(parent, StringHelper.createString(" ", size));
	}

	public static Text createFileSelectionText(
			Composite targetFileContainer, ModifyListener modifyListener) {

		Text targetFileText = new Text(targetFileContainer, SWT.BORDER);
		targetFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		if (modifyListener != null) {
			targetFileText.addModifyListener(modifyListener);
		}

		return targetFileText;
	}

	public static Combo createReadOnlyGridCombo(
			Composite parentComposite,	IValueApplier valueApplier, ApplyValueMode applyValueMode) {

		return CommonEditHelper.createReadOnlyGridCombo(
				parentComposite, valueApplier, applyValueMode);
	}

	public static Combo createCombo(Composite parent, int maxLimit, int defaultValue)
	{
		final Combo combo = new Combo(parent, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		return combo;
	}

	public static Combo createReadOnlyGridCombo(Composite parentComposite,	IValueApplier valueApplier) {

		return CommonEditHelper.createReadOnlyGridCombo(
				parentComposite, valueApplier, ApplyValueMode.ON_SELECTION_AND_FOCUS_LOST);
	}

	public static Table createTable(Composite parent)
	{
		TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL| SWT.V_SCROLL | SWT.FULL_SELECTION| SWT.BORDER | SWT.TOP);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.getControl().setLayoutData(gridData);
		return table;
	}

	public GridData createTableGrid()
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 2;
		gridData.heightHint=500;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.CENTER;
		return gridData;

	}
	public static TableColumn[] addColumn(Table table, int ColumnNr, String[] name){
		TableColumn[] column = new TableColumn[ColumnNr];
		for(int i = 0; i < ColumnNr; i ++ )
		{
			column[i] = new TableColumn(table, SWT.NONE);
			column[i].setText(name[i]);
			column[i].setResizable(true);
			column[i].setMoveable(true);
		}

		return column;
	}

	public static Combo createReadWriteGridCombo(
			Composite parentComposite, IValueApplier valueApplier, ApplyValueMode applyValueMode) {

		return CommonEditHelper.createReadWriteGridCombo(
				parentComposite, valueApplier, applyValueMode);
	}	

	public static Combo createReadWriteGridCombo(Composite parentComposite, IValueApplier valueApplier) {

		return CommonEditHelper.createReadWriteGridCombo(
				parentComposite, valueApplier, ApplyValueMode.ON_SELECTION_AND_FOCUS_LOST);
	}

	public static void setMonospaceFont(Control control) {
		FontData oldFontData = control.getFont().getFontData()[0];
		Font font = new Font(control.getDisplay(), new FontData("Mono", oldFontData.getHeight()-1, oldFontData.getStyle()));
		control.setFont(font);
	}

	private static class BrowseButtonClickListener extends SelectionAdapter {

		int fDialogStyle;
		String[] fFileExtensions;
		Text fTargetFileText;

		BrowseButtonClickListener(int dialogStyle, String[] fileExtensions, Text targetFileText) {

			fDialogStyle = dialogStyle;
			fFileExtensions = fileExtensions;
			fTargetFileText = targetFileText;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {

			EcFileDialog dialog = new EcFileDialog(EclipseHelper.getActiveShell(), fDialogStyle);

			dialog.setFilterExtensions(fFileExtensions);

			String filePath = dialog.open();
			if (filePath == null) {
				return;
			}

			fTargetFileText.setText(filePath);
		}

		public void setFileExtensionsFilter(String[] fileExtensions) {
			fFileExtensions  = fileExtensions;
		}
	}

	public static FileSelectionComposite createFileSelectionComposite(
			Composite parent, 
			String labelText, 
			String[] extensionsFilter, 
			ModifyListener textModifyListener) {
		return new FileSelectionComposite(parent, labelText, extensionsFilter, textModifyListener );
	}

	public static class FileSelectionComposite {

		private Composite fChildComposite;
		private Text fTargetFileText;
		GridButton fGridButton;
		BrowseButtonClickListener fBrowseButtonClickListener;

		FileSelectionComposite(
				Composite parent, 
				String labelText, 
				String[] extensionsFilter, 
				ModifyListener textModifyListener) {

			fChildComposite = createGridComposite(parent, 2);

			createLabel(fChildComposite, labelText);
			createSpacer(fChildComposite, 1);

			fTargetFileText = createFileSelectionText(fChildComposite, textModifyListener);

			fBrowseButtonClickListener = 
					new DialogObjectToolkit.BrowseButtonClickListener(
							SWT.SAVE, extensionsFilter, fTargetFileText);

			if (ApplicationContext.isApplicationTypeLocal()) {
				fGridButton = createBrowseButton(fChildComposite, fBrowseButtonClickListener);
			}
		}

		public Text getTextField() {
			return fTargetFileText;
		}

		public void setFileExtensionsFilter(String[] extensionsFilter) {

			fBrowseButtonClickListener.setFileExtensionsFilter(extensionsFilter);
		}

	}

	public static Button createGridCheckBox(
			Composite parentComposite, 
			String checkboxLabel,
			IValueApplier valueApplier) {

		Button checkbox = new Button(parentComposite, SWT.CHECK);
		checkbox.setText(checkboxLabel);
		GridData checkboxGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		checkbox.setLayoutData(checkboxGridData);

		CommonEditHelper.ApplyValueWhenSelectionListener selectionListener = 
				new CommonEditHelper.ApplyValueWhenSelectionListener(valueApplier);

		checkbox.addSelectionListener(selectionListener);

		return checkbox;
	}


	public static GridButton createBrowseButton(
			Composite parent, SelectionListener selectionListener) {

		final String BROWSE_LABEL = "Browse...";
		return createGridButton(parent, BROWSE_LABEL, selectionListener);
	}

	public static GridButton createGridButton(
			Composite parent, String buttonText, SelectionListener selectionListener) {

		return new GridButton(parent, buttonText, selectionListener);
	}

	public static class GridButton {

		Button fButton;
		SelectionListener fSelectionListener;

		GridButton(Composite parent, String buttonText, SelectionListener selectionListener) {

			fButton = new Button(parent, SWT.NONE);
			fButton.setText(buttonText);
			fButton.setLayoutData(new GridData());

			fSelectionListener = selectionListener;

			if (fSelectionListener != null) {
				fButton.addSelectionListener(fSelectionListener);
			}
		}

		public void setLayoutData(GridData gridData){
			fButton.setLayoutData(gridData);
		}

		public void setEnabled(boolean value){
			fButton.setEnabled(value);
		}

		public void setSelectionListener(SelectionListener selectionListener) {

			if (selectionListener == null) {
				return;
			}

			if (fSelectionListener != null) {
				fButton.removeSelectionListener(fSelectionListener);
			}

			fButton.addSelectionListener(selectionListener);
		}

		public GridData getLayoutData() {
			return (GridData) fButton.getLayoutData();
		}

	}


}