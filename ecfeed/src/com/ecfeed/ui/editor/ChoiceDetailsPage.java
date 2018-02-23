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

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.IValueApplier;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.common.utils.SwtObjectHelper;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.ChoiceInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodParameterInterface;

public class ChoiceDetailsPage extends BasicDetailsPage {

	private ChoicesViewer fChildrenViewer;
	private ChoiceLabelsViewer fLabelsViewer;
	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fValueCombo;


	private ChoiceInterface fChoiceIf;
	private AbstractCommentsSection fCommentsSection;


	public ChoiceDetailsPage(
			IMainTreeProvider mainTreeProvider,
			ChoiceInterface choiceInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider) {
		this(mainTreeProvider, choiceInterface, updateContext, javaProjectProvider, null);
	}

	public ChoiceDetailsPage(
			IMainTreeProvider mainTreeProvider,
			ChoiceInterface choiceInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider,
			EcFormToolkit ecFormToolkit) {
		super(mainTreeProvider, updateContext, javaProjectProvider, ecFormToolkit);
		fChoiceIf = choiceInterface;
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createNameValueEditor(getMainComposite());

		addCommentsSection();

		addViewerSection(fChildrenViewer = 
				new ChoicesViewer(
						this, getMainTreeProvider(), getModelUpdateContext(), getJavaProjectProvider()));

		addViewerSection(fLabelsViewer = new ChoiceLabelsViewer(this, getModelUpdateContext(), getJavaProjectProvider()));

		getEcFormToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite client = super.createTextClientComposite();
		return client;
	}

	@Override
	public void refresh(){
		super.refresh();
		ChoiceNode selectedChoice = getSelectedChoice();
		if(selectedChoice != null){
			fChoiceIf.setOwnNode(selectedChoice);

			String title = getSelectedChoice().toString();
			getMainSection().setText(title);

			fCommentsSection.setInput(selectedChoice);
			fChildrenViewer.setInput(selectedChoice);
			fLabelsViewer.setInput(selectedChoice);

			//
			if(getSelectedElement() instanceof MethodParameterNode){
				MethodParameterNode parameter = (MethodParameterNode)getSelectedElement();
				fExpectedCheckbox.setSelection(parameter.isExpected());
				fExpectedCheckbox.setEnabled(expectedCheckboxEnabled());
			}


			//

			fNameText.setText(selectedChoice.getName()+"q");
			refreshValueEditor(selectedChoice);
		}
	}

	private boolean expectedCheckboxEnabled(){
		return fParameterIf.isLinked() == false || fParameterIf.isUserType();
	}

	private void addCommentsSection() {

		if (ApplicationContext.isProjectAvailable()) {
			addForm(fCommentsSection = new ChoiceCommentsSection(this, getModelUpdateContext(), getJavaProjectProvider()));
		} else {
			addForm(fCommentsSection = new SingleTextCommentsSection(this, getModelUpdateContext(), getJavaProjectProvider()));
		}
	}

	private void refreshValueEditor(ChoiceNode choiceNode) {
		String type = fChoiceIf.getParameter().getType();
		if(fValueCombo != null && fValueCombo.isDisposed() == false){
			fValueCombo.dispose();
		}
		int style = SWT.DROP_DOWN;
		if(AbstractParameterInterface.isBoolean(type)){
			style |= SWT.READ_ONLY;
		}
		fValueCombo = new ComboViewer(fAttributesComposite, style).getCombo();
		fValueCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Set<String> items = new LinkedHashSet<String>(AbstractParameterInterface.getSpecialValues(type));
		if(JavaTypeHelper.isUserType(type)){
			Set<String> usedValues = fChoiceIf.getParameter().getLeafChoiceValues();
			usedValues.removeAll(items);
			items.addAll(usedValues);
		}
		items.add(fChoiceIf.getValue());
		fValueCombo.setItems(items.toArray(new String[]{}));
		setValueComboText(choiceNode);
		fValueCombo.addSelectionListener(new ValueSelectedListener());
		fValueCombo.addFocusListener(new ValueFocusLostListener());

		if (choiceNode.isAbstract()) {
			fValueCombo.setEnabled(false);
		} else {
			fValueCombo.setEnabled(true);
		}

		fAttributesComposite.layout();
	}

	private void setValueComboText(ChoiceNode choiceNode) {
		if (choiceNode.isAbstract()) {
			fValueCombo.setText(ChoiceNode.ABSTRACT_CHOICE_MARKER);
		} else {
			fValueCombo.setText(fChoiceIf.getValue());
		}
	}

	private ChoiceNode getSelectedChoice(){
		if(getSelectedElement() != null && getSelectedElement() instanceof ChoiceNode) {
			return (ChoiceNode)getSelectedElement();
		}
		return null;
	}

	private void createNameValueEditor(Composite parent) {

		fAttributesComposite = getEcFormToolkit().createGridComposite(parent, 2);

		getEcFormToolkit().createLabel(fAttributesComposite, "Name");
		fNameText = getEcFormToolkit().createGridText(fAttributesComposite, new NameApplier());

		//<<<<<<< HEAD
		//		getEcFormToolkit().createLabel(fAttributesComposite, "Value");
		//		getEcFormToolkit().paintBordersFor(fAttributesComposite);
		//=======
		//		fExpectedCheckbox = 
		//				getFormObjectToolkit().createGridCheckBox(
		//						fAttributesComposite, "Randomize value", new ExpectedApplier());
		//		SwtObjectHelper.setHorizontalSpan(fExpectedCheckbox, 3);
		//		//TODO 3
		//		//add randomize button
		//		
		//		getEcFormToolkit().createLabel(fAttributesComposite, "Value");
		//		getEcFormToolkit().paintBorders(fAttributesComposite);
		//>>>>>>> 5f4cd85... Add randomize choice button.

		fExpectedCheckbox = 
				getEcFormToolkit().createGridCheckBox(
						fAttributesComposite, "Randomize value", new ExpectedApplier());
		SwtObjectHelper.setHorizontalSpan(fExpectedCheckbox, 3);
		//TODO 3
		//add randomize button

		getEcFormToolkit().createLabel(fAttributesComposite, "Value");
		getEcFormToolkit().paintBordersFor(fAttributesComposite);

	}
	private Button fExpectedCheckbox;
	private MethodParameterInterface fParameterIf;

	private class ExpectedApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fParameterIf.setExpected(fExpectedCheckbox.getSelection());
			fExpectedCheckbox.setSelection(fParameterIf.isExpected());
		}
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return ChoiceNode.class;
	}

	private class NameApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fChoiceIf.setName(fNameText.getText());
			fNameText.setText(fChoiceIf.getNodeName());
		}
	}

	private class ValueSelectedListener extends ComboSelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			setValueComboToModel();
		}
	}

	private class ValueFocusLostListener extends FocusLostListener {

		@Override
		public void focusLost(FocusEvent e) {
			setValueComboToModel();
		}

	}

	private void setValueComboToModel() {
		fChoiceIf.setValue(fValueCombo.getText());

		ChoiceNode choiceNode = getSelectedChoice();
		if (choiceNode != null) {
			setValueComboText(choiceNode);
		}

	}

}
