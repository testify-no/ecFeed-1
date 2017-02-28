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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecfeed.core.utils.StringHelper;

public class FormObjectToolkit {

	private FormToolkit fFormToolkit = null;

	protected FormObjectToolkit(FormToolkit formToolkit) {
		fFormToolkit = formToolkit;
	}

	public void paintBorders(Composite composite) {
		fFormToolkit.paintBordersFor(composite);
	}

	public Composite createGridComposite(Composite parentComposite, int countOfColumns) {
		Composite composite = fFormToolkit.createComposite(parentComposite);

		composite.setLayout(new GridLayout(countOfColumns, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		return composite;
	}

	public Composite createRowComposite(Composite parentComposite) {
		Composite composite = fFormToolkit.createComposite(parentComposite);

		RowLayout rowLayout = new RowLayout();
		composite.setLayout(rowLayout);

		return composite;
	}	

	public Label createLabel(Composite parentComposite, String text) {
		return fFormToolkit.createLabel(parentComposite, text, SWT.NONE);
	}

	public Label createEmptyLabel(Composite parentComposite) {
		return fFormToolkit.createLabel(parentComposite, " ", SWT.NONE);
	}

	public Label createSpacer(Composite parentComposite, int size) {
		Label label = createLabel(parentComposite, StringHelper.createString("X", size));
		label.setVisible(false);
		return label;
	}

	public Text createGridText(Composite parentGridComposite, IValueApplier valueApplier) {

		Text text = fFormToolkit.createText(parentGridComposite, null, SWT.NONE);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		TypedListener onLostFocusListener = 
				new TypedListener(new ApplyValueWhenFocusLostListener(valueApplier));
		text.addListener(SWT.FocusOut, onLostFocusListener);

		ApplyValueWhenSelectionListener onApplyListener = 
				new ApplyValueWhenSelectionListener(valueApplier);
		text.addSelectionListener(onApplyListener);
		
		OnSaveKeyListener onSaveKeyListener = 
				new OnSaveKeyListener(valueApplier);
		text.addKeyListener(onSaveKeyListener);
		
		return text;
	}	

	public Button createButton(Composite parentComposite, String text, ButtonClickListener selectionListener) {
		Button button = fFormToolkit.createButton(parentComposite, text, SWT.NONE);

		if (selectionListener != null) {
			button.addSelectionListener(selectionListener);
		}

		return button;
	}

	public Combo createReadOnlyGridCombo(Composite parentComposite, ComboSelectionListener selectionListener) {
		Combo combo = new Combo(parentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		configureCombo(combo, selectionListener, null);
		return combo;
	}

	public Combo createReadWriteGridCombo(
			Composite parentComposite, 
			ComboSelectionListener selectionListener, 
			FocusLostListener focusLostListener) {
		Combo combo = new Combo(parentComposite, SWT.DROP_DOWN);
		configureCombo(combo, selectionListener, focusLostListener);
		return combo;
	}	

	private void configureCombo(
			Combo combo, 
			ComboSelectionListener selectionListener, 
			FocusLostListener focusLostListener) {

		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		if (selectionListener != null) {
			combo.addSelectionListener(selectionListener);
		}

		if (focusLostListener != null) {
			combo.addFocusListener(focusLostListener);
		}
	}

	public Button createGridCheckBox(Composite parentComposite, String checkboxLabel, CheckBoxClickListener selectionListener) {
		Button checkbox = fFormToolkit.createButton(parentComposite, checkboxLabel, SWT.CHECK);
		GridData checkboxGridData = new GridData(SWT.FILL,  SWT.CENTER, true, false);
		checkbox.setLayoutData(checkboxGridData);

		if (selectionListener != null) {
			checkbox.addSelectionListener(selectionListener);
		}

		return checkbox;
	}

	public GridData getGridData(Control control) {
		return (GridData)control.getLayoutData();
	}

	public void setHorizontalSpan(Control control, int span) {
		GridData gridData = getGridData(control);
		gridData.horizontalSpan = span;
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

	private class ApplyValueWhenFocusLostListener extends FocusLostListener {
		
		IValueApplier fValueApplier;
		
		ApplyValueWhenFocusLostListener(IValueApplier valueApplier) {
			fValueApplier = valueApplier;
		}

		@Override
		public void focusLost(FocusEvent e) {
			fValueApplier.applyValue();
		}
		
	}
	
	private class ApplyValueWhenSelectionListener extends AbstractSelectionAdapter {

		IValueApplier fValueApplier;
		
		ApplyValueWhenSelectionListener(IValueApplier valueApplier) {
			fValueApplier = valueApplier;
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			fValueApplier.applyValue();
		}

	}	

}
