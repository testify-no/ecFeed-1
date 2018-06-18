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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.adapter.ITypeAdapter;
import com.ecfeed.core.adapter.ITypeAdapter.EConversionMode;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.common.utils.SwtObjectHelper;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.ChoiceInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ChoiceDetailsPage extends BasicDetailsPage {

	private IFileInfoProvider fFileInfoProvider;	
	private ChoicesViewer fChildrenViewer;
	private ChoiceLabelsViewer fLabelsViewer;
	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fValueCombo;
	private ChoiceInterface fChoiceInterface;
	private AbstractCommentsSection fCommentsSection;

	private Button fRandomizeCheckbox;

	private Label fValueLabel;

	public ChoiceDetailsPage(
			ModelMasterSection masterSection, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
		fChoiceInterface = new ChoiceInterface(this, fFileInfoProvider);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createNameValueEditorWithoutValueCombo(getMainComposite());

		addCommentsSection();

		addViewerSection(fChildrenViewer = new ChoicesViewer(this, this, fFileInfoProvider));
		addViewerSection(fLabelsViewer = new ChoiceLabelsViewer(this, this, fFileInfoProvider));

		getToolkit().paintBordersFor(getMainComposite());
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


			fNameText.setText(selectedChoice.getName());
			refreshValueAndRandomizedFields(selectedChoice);
		}
	}


	private void addCommentsSection() {

		if (fFileInfoProvider.isProjectAvailable()) {
			addForm(fCommentsSection = new ChoiceCommentsSection(this, this, fFileInfoProvider));
		} else {
			addForm(fCommentsSection = new SingleTextCommentsSection(this, this, fFileInfoProvider));
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

		fAttributesComposite = getToolkit().createComposite(parent);
		fAttributesComposite.setLayout(new GridLayout(2, false));
		fAttributesComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		getFormObjectToolkit().createLabel(fAttributesComposite, "Name");
		fNameText = getFormObjectToolkit().createGridText(fAttributesComposite, new NameApplier());

		fRandomizeCheckbox = 
				getFormObjectToolkit().createGridCheckBox(
						fAttributesComposite, "Randomize value", new RandomizedApplier());
		SwtObjectHelper.setHorizontalSpan(fRandomizeCheckbox, 3);

		fValueLabel = getFormObjectToolkit().createLabel(fAttributesComposite, "Value");
		getFormObjectToolkit().paintBorders(fAttributesComposite);

		// fValueCombo created during refresh
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
			fNameText.setText(fChoiceInterface.getName());
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
