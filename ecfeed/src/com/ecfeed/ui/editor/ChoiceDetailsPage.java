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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;
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
	private ChoiceInterface fChoiceInterface;
	private AbstractCommentsSection fCommentsSection;

	private Button fExpectedCheckbox;
	private Button fRandomizeCheckbox;
	private MethodParameterInterface fParameterIf;
	private Label fValueLabel;

	public ChoiceDetailsPage(
			IMainTreeProvider mainTreeProvider,
			ChoiceInterface choiceInterface,
			IModelUpdateContext updateContext, 
			IJavaProjectProvider javaProjectProvider,
			EcFormToolkit ecFormToolkit) {
		super(mainTreeProvider, updateContext, javaProjectProvider, ecFormToolkit);
		fChoiceInterface = choiceInterface;
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createNameValueEditorWithoutValueCombo(getMainComposite());

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
			fChoiceInterface.setOwnNode(selectedChoice);

			String title = getSelectedChoice().toString();
			getMainSection().setText(title);

			fCommentsSection.setInput(selectedChoice);
			fChildrenViewer.setInput(selectedChoice);
			fLabelsViewer.setInput(selectedChoice);

			if(getSelectedElement() instanceof MethodParameterNode){
				MethodParameterNode parameter = (MethodParameterNode)getSelectedElement();
				fExpectedCheckbox.setSelection(parameter.isExpected());
				fExpectedCheckbox.setEnabled(expectedCheckboxEnabled());
			}

			fNameText.setText(selectedChoice.getName());
			refreshValueAndRandomizedFields(selectedChoice);
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

	private void refreshValueAndRandomizedFields(ChoiceNode choiceNode) {

		if (fValueCombo != null && fValueCombo.isDisposed() == false) {
			fValueCombo.dispose();
		}

		createValueCombo(choiceNode);

		updateRandomizeCheckBox(choiceNode);
		updateValueLabel(choiceNode);		

		fAttributesComposite.layout();
	}

	private void createValueCombo(ChoiceNode choiceNode) {

		String typeName = fChoiceInterface.getParameter().getType();
		int style = calculateValueComboStyle(typeName);

		fValueCombo = new ComboViewer(fAttributesComposite, style).getCombo();
		fValueCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Set<String> items = createComboItems(typeName);
		fValueCombo.setItems(items.toArray(new String[]{}));

		fValueCombo.setText(getValueComboText(choiceNode));		
		fValueCombo.addSelectionListener(new ValueSelectedListener());
		fValueCombo.addFocusListener(new ValueFocusLostListener());

		if (choiceNode.isAbstract()) {
			fValueCombo.setEnabled(false);
		} else {
			fValueCombo.setEnabled(true);
		}
	}

	private Set<String> createComboItems(String typeName) {

		Set<String> items = 
				new LinkedHashSet<String>(AbstractParameterInterface.getSpecialValues(typeName));

		if (JavaTypeHelper.isUserType(typeName)) {

			Set<String> usedValues = fChoiceInterface.getParameter().getLeafChoiceValues();
			usedValues.removeAll(items);
			items.addAll(usedValues);
		}

		items.add(fChoiceInterface.getValue());

		return fChoiceInterface.convertItemsToMatchChoice(items, EConversionMode.QUIET);
	}

	private static int calculateValueComboStyle(String typeName) {

		int style = SWT.DROP_DOWN;

		if (JavaTypeHelper.isBooleanTypeName(typeName)) {
			style |= SWT.READ_ONLY;
		}

		return style;
	}

	private void updateRandomizeCheckBox(ChoiceNode choiceNode) {

		fRandomizeCheckbox.setSelection(isRandomizeCheckboxSelected(choiceNode));
		fRandomizeCheckbox.setEnabled(isRandomizeCheckboxEnabled(choiceNode));
	}

	private void updateValueLabel(ChoiceNode choiceNode) {

		String type = fChoiceInterface.getParameter().getType();
		boolean isRandomizedValue = choiceNode.isRandomizedValue();

		if (isRandomizedValue) {
			if (type.equals(JavaTypeHelper.TYPE_NAME_STRING)) {
				fValueLabel.setText("Regex");
			} else {
				fValueLabel.setText("Range");
			}
		} else {
			fValueLabel.setText("Value");
		}
	}

	private boolean isRandomizeCheckboxEnabled(ChoiceNode choiceNode) {

		if (choiceNode.isAbstract()) {
			return false;
		}

		ITypeAdapter<?> typeAdapter = fChoiceInterface.getTypeAdapter();

		if (!typeAdapter.isRandomizable())
			return false;

		return true;
	}

	private boolean isRandomizeCheckboxSelected(ChoiceNode choiceNode) {

		if (choiceNode.isAbstract()) {
			return false;
		}

		return choiceNode.isRandomizedValue();
	}

	private String getValueComboText(ChoiceNode choiceNode) {

		if (choiceNode.isAbstract()) {
			return ChoiceNode.ABSTRACT_CHOICE_MARKER;
		} else {
			return fChoiceInterface.getValue();
		}
	}

	private ChoiceNode getSelectedChoice(){
		if(getSelectedElement() != null && getSelectedElement() instanceof ChoiceNode) {
			return (ChoiceNode)getSelectedElement();
		}
		return null;
	}

	private void createNameValueEditorWithoutValueCombo(Composite parent) {

		fAttributesComposite = getEcFormToolkit().createGridComposite(parent, 2);

		getEcFormToolkit().createLabel(fAttributesComposite, "Name");
		fNameText = getEcFormToolkit().createGridText(fAttributesComposite, new NameApplier());

		fRandomizeCheckbox = 
				getEcFormToolkit().createGridCheckBox(
						fAttributesComposite, "Randomize value", new RandomizedApplier());
		SwtObjectHelper.setHorizontalSpan(fRandomizeCheckbox, 3);

		fValueLabel = getEcFormToolkit().createLabel(fAttributesComposite, "Value");
		getEcFormToolkit().paintBordersFor(fAttributesComposite);
	}

	private class RandomizedApplier implements IValueApplier {


		@Override
		public void applyValue() {

			fChoiceInterface.setRandomized(fRandomizeCheckbox.getSelection());

			fRandomizeCheckbox.setSelection(fChoiceInterface.isRandomized());
			fValueCombo.setText(fChoiceInterface.getValue());

			updateValueLabel(fChoiceInterface.getOwnNode());
		}
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return ChoiceNode.class;
	}

	private class NameApplier implements IValueApplier {

		@Override
		public void applyValue() {

			fChoiceInterface.setName(fNameText.getText());
			fNameText.setText(fChoiceInterface.getNodeName());
		}
	}

	private class ValueSelectedListener extends ComboSelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			applyValueToModelAndCombo();
		}
	}

	private class ValueFocusLostListener extends FocusLostListener {

		@Override
		public void focusLost(FocusEvent e) {
			applyValueToModelAndCombo();
		}
	}

	private void applyValueToModelAndCombo() {

		fChoiceInterface.setValue(fValueCombo.getText());
		fValueCombo.setText(getValueComboText(fChoiceInterface.getOwnNode()));
	}

}
