/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.ecfeed.core.utils.IValueApplier;

public class CommonEditHelper {

	public static Combo createReadOnlyGridCombo(
			Composite parentComposite,	IValueApplier valueApplier, ApplyValueMode applyValueMode) {

		Combo combo = new Combo(parentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		configureCombo(combo, valueApplier, applyValueMode);

		return combo;
	}

	public static Combo createReadWriteGridCombo( 
			Composite parentComposite, IValueApplier valueApplier, ApplyValueMode applyValueMode) {

		Combo combo = new Combo(parentComposite, SWT.DROP_DOWN);
		configureCombo(combo, valueApplier, applyValueMode);

		return combo;
	}	

	private static void configureCombo(Combo combo, IValueApplier valueApplier, ApplyValueMode applyValueMode) {

		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		ApplyValueForComboListener selectionListener = new ApplyValueForComboListener(valueApplier);
		combo.addSelectionListener(selectionListener);

		if (applyValueMode == ApplyValueMode.ON_SELECTION_AND_FOCUS_LOST) {
			ApplyValueWhenFocusLostListener focusLostListener = 
					new ApplyValueWhenFocusLostListener(valueApplier);

			combo.addFocusListener(focusLostListener);
		}
	}

	public static class ApplyValueWhenFocusLostListener extends FocusLostListener {

		IValueApplier fValueApplier;

		public ApplyValueWhenFocusLostListener(IValueApplier valueApplier) {
			fValueApplier = valueApplier;
		}

		@Override
		public void focusLost(FocusEvent e) {
			fValueApplier.applyValue();
		}

	}

	public static class ApplyValueForComboListener extends ComboSelectionListener {

		IValueApplier fValueApplier;

		public ApplyValueForComboListener(IValueApplier valueApplier) {
			fValueApplier = valueApplier;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			fValueApplier.applyValue();
		}
	}

	public static class ApplyValueWhenSelectionListener extends AbstractSelectionAdapter {

		IValueApplier fValueApplier;

		public ApplyValueWhenSelectionListener(IValueApplier valueApplier) {
			fValueApplier = valueApplier;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			fValueApplier.applyValue();
		}

	}	

}
