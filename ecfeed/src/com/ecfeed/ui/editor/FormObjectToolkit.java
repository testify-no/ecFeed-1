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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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

	public Label createSpacer(Composite parentComposite, int size) {
		return createLabel(parentComposite, StringHelper.createString(" ", size));
	}

	public Text createGridText(Composite parentGridComposite, SelectionListener selectionListener) {
		Text text = fFormToolkit.createText(parentGridComposite, null, SWT.NONE);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		if (selectionListener != null) {
			text.addSelectionListener(selectionListener);
		}

		return text;
	}

	public Button createButton(Composite parentComposite, String text, SelectionListener selectionListener) {
		Button button = fFormToolkit.createButton(parentComposite, text, SWT.NONE);

		if (selectionListener != null) {
			button.addSelectionListener(selectionListener);
		}

		return button;
	}

	public Combo createGridCombo(Composite parentComposite, SelectionListener selectionListener) {
		Combo combo = new Combo(parentComposite, SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		if (selectionListener != null) {
			combo.addSelectionListener(selectionListener);
		}

		return combo;
	}

	public Button createGridCheckBox(Composite parentComposite, String checkboxLabel, SelectionListener selectionListener) {
		Button checkbox = fFormToolkit.createButton(parentComposite, checkboxLabel, SWT.CHECK);
		GridData checkboxGridData = new GridData(SWT.FILL,  SWT.CENTER, true, false);
		checkbox.setLayoutData(checkboxGridData);

		if (selectionListener != null) {
			checkbox.addSelectionListener(selectionListener);
		}

		return checkbox;
	}
}
