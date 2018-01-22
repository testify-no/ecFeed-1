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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.NodePropertyDefFindByType;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.model.NodePropertyValueSet;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.IValueApplier;
import com.ecfeed.ui.common.utils.IJavaProjectProvider;
import com.ecfeed.ui.common.utils.SwtObjectHelper;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class WebParameterSection extends BasicSection {

	AbstractParameterInterface fAbstractParameterInterface;
	MethodParameterNode fMethodParameterNode;

	private EcFormToolkit fEcFormToolkit;
	private Composite fClientComposite;
	private Composite fGridComposite;

	private Combo fWebElementTypeCombo;
	private Button fOptionalCheckbox;

	private Combo fFindByElemTypeCombo;

	private Text fFindByElemValueText;

	private Combo fActionCombo = null;

	private final NodePropertyDefs.PropertyId fWebElementTypePropertyId = NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE;
	private final NodePropertyDefs.PropertyId fOptionalPropertyId = NodePropertyDefs.PropertyId.PROPERTY_OPTIONAL;

	private final NodePropertyDefs.PropertyId fFindByElemTypePropertyId = NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT;
	private final NodePropertyDefs.PropertyId fFindByElemValuePropertyId = NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_VALUE_OF_ELEMENT;
	private final NodePropertyDefs.PropertyId fActionPropertyId = NodePropertyDefs.PropertyId.PROPERTY_ACTION;

	public WebParameterSection(ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			AbstractParameterInterface abstractParameterInterface,
			IJavaProjectProvider javaProjectProvider) {
		super(sectionContext, updateContext, javaProjectProvider, StyleDistributor.getSectionStyle());

		fAbstractParameterInterface = abstractParameterInterface;

		fEcFormToolkit = getEcFormToolkit(); 

		setText("Web runner properties");
		fClientComposite = getClientComposite();

		fGridComposite = fEcFormToolkit.createGridComposite(fClientComposite, 2);

		createControls(fGridComposite);
		fEcFormToolkit.paintBordersFor(fGridComposite);
	}

	private void createControls(Composite gridComposite) {
		fMethodParameterNode = (MethodParameterNode)fAbstractParameterInterface.getOwnNode();

		//
		fEcFormToolkit.createLabel(fGridComposite, "Element type");
		fWebElementTypeCombo = fEcFormToolkit.createReadOnlyGridCombo(fGridComposite, new ElementTypeApplier());

		//
		fOptionalCheckbox = fEcFormToolkit.createGridCheckBox(fGridComposite, "Optional", new OptionalValueApplier() );
		fOptionalCheckbox.setEnabled(false);
		setParamsForTheFirstColumn(fOptionalCheckbox);

		fEcFormToolkit.createSpacer(fGridComposite, 1);

		// 
		fEcFormToolkit.createLabel(fGridComposite, "Identified by ");
		fEcFormToolkit.createSpacer(fGridComposite, 1);

		//
		fFindByElemTypeCombo = fEcFormToolkit.createReadOnlyGridCombo(fGridComposite, new FindByTypeApplier());
		setParamsForTheFirstColumn(fFindByElemTypeCombo);

		fFindByElemValueText = fEcFormToolkit.createGridText(fGridComposite, new FindByValueApplier());

		//
		fEcFormToolkit.createLabel(fGridComposite, "Action ");
		fActionCombo = fEcFormToolkit.createReadOnlyGridCombo(fGridComposite, new ActionApplier());
	}

	private void setParamsForTheFirstColumn(Control control) {
		GridData gridData = SwtObjectHelper.getGridData(control);
		gridData.grabExcessHorizontalSpace = false;
		gridData.widthHint = 150;
	}

	public void refresh() {

		fMethodParameterNode = (MethodParameterNode)fAbstractParameterInterface.getOwnNode();
		String parameterType = fMethodParameterNode.getType();
		String webElementType = getWebElementValue(parameterType);

		refreshControls(webElementType);
	}

	private String getWebElementValue(String parameterType) {

		NodePropertyDefs.PropertyId propertyId = NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE;
		NodePropertyValueSet valueSet = NodePropertyDefs.getValueSet(propertyId, parameterType);

		if (valueSet == null) {
			return null;
		}

		String webElementValue = fMethodParameterNode.getPropertyValue(propertyId);

		if (!valueSet.isOneOfPossibleValues(webElementValue)) {
			webElementValue = null;
		}

		if (webElementValue == null) {
			webElementValue = NodePropertyDefs.getPropertyDefaultValue(propertyId, parameterType);
			fMethodParameterNode.setPropertyValue(fWebElementTypePropertyId, webElementValue);
		}

		return webElementValue;
	}

	private void refreshControls(String webElementType) {

		if (webElementType == null) {
			return;
		}
		refreshWebElementTypeCombo(webElementType);
		refreshOptionalCheckBox(webElementType);
		refreshFindByTypeAndValue(webElementType);
		refreshAction(webElementType);
	}

	private void refreshWebElementTypeCombo(String value) {

		String parentValue = fMethodParameterNode.getType();

		NodePropertyValueSet valueSet =
				NodePropertyDefs.getValueSet(
						fWebElementTypePropertyId, parentValue, fMethodParameterNode.isExpected());

		refreshCombo(fWebElementTypeCombo, valueSet, parentValue, value);

		fAbstractParameterInterface.setProperty(fWebElementTypePropertyId, fWebElementTypeCombo.getText());
	}

	private static void refreshCombo(Combo combo, NodePropertyValueSet valueSet, String parentValue, String currentPropertyvalue) {

		String[] possibleValues = valueSet.getPossibleValues();

		combo.setItems(possibleValues);

		boolean isOneOfPossibleValues = valueSet.isOneOfPossibleValues(currentPropertyvalue);

		if (currentPropertyvalue != null && isOneOfPossibleValues) {
			combo.setText(currentPropertyvalue);
			return;
		}

		String defaultValue = valueSet.getDefaultValue();
		if (defaultValue != null) {
			combo.setText(defaultValue);
		}		
	}

	private void refreshOptionalCheckBox(String webElementType) {

		if (!isOptionalCheckboxEnabled(webElementType)) {
			fOptionalCheckbox.setEnabled(false);
			return;
		}

		String currentPropertyValue = fMethodParameterNode.getPropertyValue(fOptionalPropertyId);

		boolean isChecked = BooleanHelper.parseBoolean(currentPropertyValue);
		fOptionalCheckbox.setSelection(isChecked);

		fOptionalCheckbox.setEnabled(true);
	}

	private boolean isOptionalCheckboxEnabled(String webElementType) {

		if (!NodePropertyDefs.isOptionalAvailable(webElementType)) {
			return false;
		}		

		return true;
	}

	private void refreshFindByTypeAndValue(String webElementType) {

		refreshfFindElemTypeCombo(webElementType);
		refreshfFindByValueText();

		boolean isAvailable = isChildOfWebElementAvailable(webElementType);
		fFindByElemTypeCombo.setEnabled(isAvailable);
		fFindByElemValueText.setEnabled(isAvailable);
	}

	private void refreshfFindElemTypeCombo(String webElementType) {

		NodePropertyValueSet valueSet = NodePropertyDefs.getValueSet(fFindByElemTypePropertyId, webElementType);
		fFindByElemTypeCombo.setItems(valueSet.getPossibleValues());

		fFindByElemTypeCombo.setText(getDefaultFindByType(valueSet, webElementType));
		fAbstractParameterInterface.setProperty(fFindByElemTypePropertyId, fFindByElemTypeCombo.getText());
	}

	private String getDefaultFindByType(NodePropertyValueSet valueSet, String webElementType) {

		String currentPropertyValue = fMethodParameterNode.getPropertyValue(fFindByElemTypePropertyId);

		if (currentPropertyValue == null) {
			currentPropertyValue = NodePropertyDefFindByType.UNMAPPED;
		}

		if (currentPropertyValue.equals(NodePropertyDefFindByType.UNMAPPED) &&
				(!valueSet.getDefaultValue().equals(NodePropertyDefFindByType.UNMAPPED))) {
			return valueSet.getDefaultValue();
		}

		if (valueSet.isOneOfPossibleValues(currentPropertyValue)) {
			return currentPropertyValue;
		}

		return valueSet.getDefaultValue();
	}

	private boolean isChildOfWebElementAvailable(String webElementType) {

		if (NodePropertyDefs.isFindByAvailable(webElementType)) {
			return true;
		}
		return false;
	}

	private void refreshfFindByValueText() {

		String propertyValue = fMethodParameterNode.getPropertyValue(fFindByElemValuePropertyId);
		String newText = getFindByValueText(propertyValue);

		if (newText == null) {
			fFindByElemValueText.setText("");
			return;
		}

		fFindByElemValueText.setText(newText);

		String newValue = fFindByElemValueText.getText();
		fAbstractParameterInterface.setProperty(fFindByElemValuePropertyId, newValue);
	}

	private String getFindByValueText(String value) {

		if (value == null) {
			return getDefaultFindByTextValue(fMethodParameterNode);
		}

		return value;
	}

	public static String getDefaultFindByTextValue(MethodParameterNode methodParameterNode) {

		String webElementType = 
				methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE);

		if (!NodePropertyDefs.isTypeForSimpleFindByValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE, webElementType)) {
			return null;
		}

		String findByType = 
				methodParameterNode.getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT);

		if (!NodePropertyDefs.isTypeForSimpleFindByValue(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT, findByType)) {
			return null;
		}

		return methodParameterNode.getName();
	}

	private void refreshAction(String webElementType) {

		refreshActionCombo(webElementType);

		boolean isAvailable = NodePropertyDefs.isActionAvailable(webElementType);
		fActionCombo.setEnabled(isAvailable);
	}

	private void refreshActionCombo(String webElementType) {

		String currentPropertyValue = 
				fMethodParameterNode.getPropertyValue(fActionPropertyId);

		NodePropertyValueSet valueSet = NodePropertyDefs.getValueSet(fActionPropertyId, webElementType);

		refreshCombo(fActionCombo, valueSet, webElementType, currentPropertyValue);
	}	

	private class ElementTypeApplier implements IValueApplier {

		@Override
		public void applyValue() {

			String webElementType = fWebElementTypeCombo.getText();
			fAbstractParameterInterface.setProperty(fWebElementTypePropertyId, webElementType);

			refreshFindByTypeAndValue(webElementType);
			refreshAction(webElementType);
		}
	}

	private class FindByTypeApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fAbstractParameterInterface.setProperty(fFindByElemTypePropertyId, fFindByElemTypeCombo.getText());
		}
	}

	private class FindByValueApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fAbstractParameterInterface.setProperty(fFindByElemValuePropertyId, fFindByElemValueText.getText());
		}
	}	

	private class ActionApplier implements IValueApplier {

		@Override
		public void applyValue() {

			fAbstractParameterInterface.setProperty(fActionPropertyId, fActionCombo.getText());
		}
	}	

	private class OptionalValueApplier implements IValueApplier {

		@Override
		public void applyValue() {

			String isOptionalStr = BooleanHelper.toString(fOptionalCheckbox.getSelection());
			fAbstractParameterInterface.setProperty(fOptionalPropertyId, isOptionalStr);
		}
	}	

}
