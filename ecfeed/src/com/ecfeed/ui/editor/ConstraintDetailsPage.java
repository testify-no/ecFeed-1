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

import java.util.Set;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.modelif.ConstraintInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ConstraintDetailsPage extends BasicDetailsPage {

	private Combo fNameCombo;
	private ConstraintInterface fConstraintIf;
	private ConstraintViewer fConstraintViewer;
	private SingleTextCommentsSection fCommentsSection;
	private IJavaProjectProvider fJavaProjectProvider;

	public ConstraintDetailsPage(
			IMainTreeProvider mainTreeProvider, 
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {
		super(mainTreeProvider, updateContext, javaProjectProvider);
		fJavaProjectProvider = javaProjectProvider;
		fConstraintIf = new ConstraintInterface(this, javaProjectProvider);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		createConstraintNameEdit(getMainComposite());

		addCommentsSection();

		addViewerSection(fConstraintViewer = new ConstraintViewer(this, this, fJavaProjectProvider));
	}

	private void addCommentsSection() {

		if (ApplicationContext.isProjectAvailable()) {
			addForm(fCommentsSection = new SingleTextCommentsSection(this, this, fJavaProjectProvider));
		} else {
			addForm(fCommentsSection = new SingleTextCommentsSection(this, this, fJavaProjectProvider));
		}
	}

	private void createConstraintNameEdit(Composite parent) {

		Composite composite = getEcFormToolkit().createGridComposite(parent, 2);

		getEcFormToolkit().createLabel(composite, "Constraint name");

		fNameCombo = new ComboViewer(composite, SWT.NONE).getCombo();
		fNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fNameCombo.addSelectionListener(new ConstraintNameChangedListener());
		fNameCombo.addFocusListener(new ConstraintNameFocusLostListener());
	}

	@Override
	public void refresh(){
		Object selectedElement = getSelectedElement();

		if(!(selectedElement instanceof ConstraintNode)){
			return;
		}

		ConstraintNode constraint = (ConstraintNode)selectedElement;
		getMainSection().setText(constraint.toString());
		MethodNode methodNode = constraint.getMethod();
		Set<String> constraintNames = methodNode.getConstraintsNames();
		fNameCombo.setItems(constraintNames.toArray(new String[]{}));
		fNameCombo.setText(constraint.getName());
		fConstraintViewer.setInput(constraint);

		fCommentsSection.setInput(constraint);
		fConstraintIf.setOwnNode(constraint);
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return ConstraintNode.class;
	}

	private class ConstraintNameChangedListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fConstraintIf.setName(fNameCombo.getText());
			fNameCombo.setText(fConstraintIf.getNodeName());
		}
	}

	private class ConstraintNameFocusLostListener extends FocusLostListener {

		@Override
		public void focusLost(FocusEvent e) {
			fConstraintIf.setName(fNameCombo.getText());
			fNameCombo.setText(fConstraintIf.getNodeName());
		}

	}

}
