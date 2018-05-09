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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;


/**
 * A section with the client composite divided vertically or horizontally
 * into two parts: one with big control (e.g. Text, TabFolder, Table or Tree)
 * and second containing set of buttons arranged below or aside of the main control.
 * The buttons are supposed to be used for controlling the content
 * of the main control.
 */
public class ButtonsCompositeSection extends BasicSection {

	public static final int BUTTONS_ASIDE = 1;
	public static final int BUTTONS_BELOW = 2;

	private Composite fButtonsComposite;
	private Composite fMainControlComposite;

	public ButtonsCompositeSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IJavaProjectProvider javaProjectProvider,
			int style) {

		super(sectionContext, updateContext, javaProjectProvider, style);
	}

	@Override
	protected Composite createClientComposite() {
		Composite client = super.createClientComposite();
		fMainControlComposite = createMainControlComposite(client);
		fButtonsComposite = createButtonsComposite(client);
		return client;
	}

	protected Composite getButtonsComposite(){
		return fButtonsComposite;
	}

	protected Composite getMainControlComposite(){
		return fMainControlComposite;
	}

	protected Composite createMainControlComposite(Composite parent) {
		fMainControlComposite = getEcFormToolkit().createComposite(parent);
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		fMainControlComposite.setLayout(gl);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 150;
		fMainControlComposite.setLayoutData(gd);
		return fMainControlComposite;
	}

	protected Button addButton(String text, SelectionAdapter adapter){
		return addButton(text, adapter, SWT.NONE);
	}

	protected Button addButton(String text, SelectionAdapter adapter, int style){
		Button button = getEcFormToolkit().createButton(fButtonsComposite, text, style);

		if(adapter != null){
			button.addSelectionListener(adapter);
		}
		if(buttonLayoutData() != null){
			button.setLayoutData(buttonLayoutData());
		}
		return button;
	}

	protected Text addText(String text, SelectionAdapter adapter) {

		Text txt = getEcFormToolkit().createText(fButtonsComposite, text, SWT.BORDER);

		if(adapter != null) {
			txt.addSelectionListener(adapter);
		}

		return txt;
	}

	protected Composite createButtonsComposite(Composite parent) {
		Composite buttonsComposite = getEcFormToolkit().createComposite(parent);
		buttonsComposite.setLayout(buttonsCompositeLayout());
		if(buttonsCompositeLayoutData() != null){
			buttonsComposite.setLayoutData(buttonsCompositeLayoutData());
		}
		return buttonsComposite;
	}

	protected Layout buttonsCompositeLayout() {
		if(getButtonsPosition() == BUTTONS_BELOW){
			RowLayout rl = new RowLayout();
			rl.pack = false;
			return rl;
		}
		else{
			return new GridLayout(1, false);
		}
	}

	protected Object buttonsCompositeLayoutData() {
		if(getButtonsPosition() == BUTTONS_BELOW){
			return new GridData(SWT.FILL, SWT.TOP, true, false);
		}
		else{
			return new GridData(SWT.FILL, SWT.TOP, false, true);
		}
	}

	protected Object buttonLayoutData() {
		if(getButtonsPosition() == BUTTONS_ASIDE){
			return new GridData(SWT.FILL,  SWT.TOP, true, false);
		}
		return null;
	}

	/*
	 * Indicates whether optional buttons are located below (default)
	 * or on the right side of the viewer
	 */
	protected int getButtonsPosition() {
		return BUTTONS_BELOW;
	}

	@Override
	protected Layout clientLayout() {
		GridLayout layout = new GridLayout(getButtonsPosition() == BUTTONS_BELOW?1:2, false);
		return layout;
	}

}
