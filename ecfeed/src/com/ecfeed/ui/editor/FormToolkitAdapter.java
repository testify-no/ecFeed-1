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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class FormToolkitAdapter {

	private FormToolkit fFormToolkit;

	public FormToolkitAdapter(FormToolkit formToolkit) {
		fFormToolkit = formToolkit;
	}
	
	public FormToolkit getEclipseToolkit() {
		return fFormToolkit;
	}

	public void paintBordersFor(Composite parent) {
		fFormToolkit.paintBordersFor(parent);
	}
	
	public Composite createComposite(Composite parent) {
		return fFormToolkit.createComposite(parent);
	}
	
	public Label createLabel(Composite composite, String text) {
		return fFormToolkit.createLabel(composite, text);
	}
	
	public Label createLabel(Composite parentComposite, String text, int style) {
		return fFormToolkit.createLabel(parentComposite, text, style);
	}
	
	public Text createText(Composite parentComposite, String value, int style) {
		return fFormToolkit.createText(parentComposite, value, style);
	}
	
	public Button createButton(Composite parentComposite, String text, int style) {
		return fFormToolkit.createButton(parentComposite, text, style);
	}
	
	public Section createSection(Composite parent, int style) {
		return fFormToolkit.createSection(parent, style);	
	}
	
	public Composite createComposite(Section section, int style) {
		return fFormToolkit.createComposite(section, style);
	}
	
	public void	adapt(Composite composite) {
		fFormToolkit.adapt(composite);
	}
	
}
