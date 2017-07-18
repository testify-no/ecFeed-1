/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.ui.common.ApplyValueMode;
import com.ecfeed.ui.common.CommonEditHelper;

public class EcFormToolkit {

	private FormToolkitAdapter fFormToolkitAdapter = null;

	public EcFormToolkit(FormToolkit formToolkit) {
		fFormToolkitAdapter = new FormToolkitAdapter(formToolkit);
	}

	public FormToolkitAdapter getFormToolkitAdapter() {
		return fFormToolkitAdapter;
	}

	public FormToolkit getEclipseToolkit() {
		return fFormToolkitAdapter.getEclipseToolkit();
	}

	public Section createSection(Composite parent, int style) {
		return fFormToolkitAdapter.createSection(parent, style);	
	}

	public void paintBordersFor(Composite composite) {
		fFormToolkitAdapter.paintBordersFor(composite);
	}

	public void	adapt(Composite composite) {
		fFormToolkitAdapter.adapt(composite);
	}

	public Composite createComposite(Composite parent) {
		return fFormToolkitAdapter.createComposite(parent);
	}

	public Composite createGridComposite(Composite parentComposite, int countOfColumns) {

		Composite composite = fFormToolkitAdapter.createComposite(parentComposite);
		composite.setLayout(new GridLayout(countOfColumns, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false)); // TODO causes exception on RAP

		return composite;
	}

	public Composite createRowComposite(Composite parentComposite) {
		Composite composite = fFormToolkitAdapter.createComposite(parentComposite);

		RowLayout rowLayout = new RowLayout();
		composite.setLayout(rowLayout);

		return composite;
	}	

	public Label createLabel(Composite parentComposite, String text) {

		return fFormToolkitAdapter.createLabel(parentComposite, text, SWT.NONE);
	}

	public Label createEmptyLabel(Composite parentComposite) {

		return fFormToolkitAdapter.createLabel(parentComposite, " ", SWT.NONE);
	}

	public Label createSpacer(Composite parentComposite, int size) {
		Label label = createLabel(parentComposite, StringHelper.createString("X", size));
		label.setVisible(false);
		return label;
	}

	public Text createText(Composite parentComposite, String value, int style) {
		return fFormToolkitAdapter.createText(parentComposite, value, style);
	}

	public Text createGridText(Composite parentGridComposite, IValueApplier valueApplier) {

		Text text = fFormToolkitAdapter.createText(parentGridComposite, null, SWT.NONE);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		TypedListener onLostFocusListener = 
				new TypedListener(new CommonEditHelper.ApplyValueWhenFocusLostListener(valueApplier));

		text.addListener(SWT.FocusOut, onLostFocusListener);

		CommonEditHelper.ApplyValueWhenSelectionListener onApplyListener = 
				new CommonEditHelper.ApplyValueWhenSelectionListener(valueApplier);

		text.addSelectionListener(onApplyListener);

		OnSaveKeyListener onSaveKeyListener = 
				new OnSaveKeyListener(valueApplier);
		text.addKeyListener(onSaveKeyListener);

		return text;
	}	

	public Button createButton(Composite parentComposite, String text, int style) {
		return fFormToolkitAdapter.createButton(parentComposite, text, style);
	}

	public Button createButton(Composite parentComposite, String text, ButtonClickListener selectionListener) {
		Button button = fFormToolkitAdapter.createButton(parentComposite, text, SWT.NONE);

		if (selectionListener != null) {
			button.addSelectionListener(selectionListener);
		}

		return button;
	}

	public Button createGridCheckBox(
			Composite parentComposite, 
			String checkboxLabel,
			IValueApplier valueApplier) {

		Button checkbox = fFormToolkitAdapter.createButton(parentComposite, checkboxLabel, SWT.CHECK);
		GridData checkboxGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		checkbox.setLayoutData(checkboxGridData);

		CommonEditHelper.ApplyValueWhenSelectionListener selectionListener = 
				new CommonEditHelper.ApplyValueWhenSelectionListener(valueApplier);

		checkbox.addSelectionListener(selectionListener);

		return checkbox;
	}

	public Combo createReadOnlyGridCombo(Composite parentComposite,	IValueApplier valueApplier) {
		return CommonEditHelper.createReadOnlyGridCombo(
				parentComposite, valueApplier, ApplyValueMode.ON_SELECTION_AND_FOCUS_LOST);
	}

	public Combo createReadWriteGridCombo(Composite parentComposite,	IValueApplier valueApplier) {
		return CommonEditHelper.createReadWriteGridCombo(
				parentComposite, valueApplier, ApplyValueMode.ON_SELECTION_AND_FOCUS_LOST);
	}	

	private class OnSaveKeyListener implements KeyListener {

		IValueApplier fValueApplier;

		public OnSaveKeyListener(IValueApplier valueApplier) {
			fValueApplier = valueApplier;
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {

			if ((e.stateMask & SWT.CTRL) != SWT.CTRL) {
				return;
			}

			if ((e.keyCode != 's') && (e.keyCode != 'S')) {
				return;
			}

			fValueApplier.applyValue();
			ModelEditorHelper.saveActiveEditor();
		}

	}

}
