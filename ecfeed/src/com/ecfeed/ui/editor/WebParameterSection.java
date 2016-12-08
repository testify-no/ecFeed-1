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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.StringTabHelper;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class WebParameterSection extends BasicSection {

	AbstractParameterInterface fAbstractParameterInterface;
	MethodParameterNode fMethodParameterNode;

	private FormObjectToolkit fFormObjectToolkit;
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
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, StyleDistributor.getSectionStyle());

		fAbstractParameterInterface = abstractParameterInterface;

		fFormObjectToolkit = new FormObjectToolkit(getToolkit());

		setText("Web runner properties");
		fClientComposite = getClientComposite();

		fGridComposite = fFormObjectToolkit.createGridComposite(fClientComposite, 3);
		createControls(fGridComposite);
		fFormObjectToolkit.paintBorders(fGridComposite);
	}

	private void createControls(Composite gridComposite) {
		fMethodParameterNode = (MethodParameterNode)fAbstractParameterInterface.getTarget();

		//
		fFormObjectToolkit.createLabel(fGridComposite, "Element type");
		fWebElementTypeCombo = fFormObjectToolkit.createReadOnlyGridCombo(fGridComposite, new ElementTypeChangedAdapter());
		GridData elementTypeGridData = (GridData)fWebElementTypeCombo.getLayoutData();
		elementTypeGridData.horizontalSpan = 2;

		//
		fOptionalCheckbox = fFormObjectToolkit.createGridCheckBox(fGridComposite, "Optional", new OptionalChangedAdapter() );
		fOptionalCheckbox.setEnabled(false);

		fFormObjectToolkit.createEmptyLabel(fGridComposite);

		fFormObjectToolkit.createSpacer(fGridComposite, 60);

		// 
		fFormObjectToolkit.createLabel(fGridComposite, "Identified by ");
		fFormObjectToolkit.createEmptyLabel(fGridComposite);
		fFormObjectToolkit.createEmptyLabel(fGridComposite);

		//
		fFindByElemTypeCombo = fFormObjectToolkit.createReadOnlyGridCombo(fGridComposite, new FindByChangedAdapter());
		fFindByElemValueText = fFormObjectToolkit.createGridText(fGridComposite, new FindByValueChangedAdapter());
		GridData valueTextGridData = (GridData)fFindByElemValueText.getLayoutData();
		valueTextGridData.horizontalSpan = 2;

		//
		fFormObjectToolkit.createLabel(fGridComposite, "Action ");
		fActionCombo = fFormObjectToolkit.createReadOnlyGridCombo(fGridComposite, new ActionChangedAdapter());
		GridData actionGridData = (GridData)fActionCombo.getLayoutData();
		actionGridData.horizontalSpan = 2;
	}

	public void refresh() {

		fMethodParameterNode = (MethodParameterNode)fAbstractParameterInterface.getTarget();
		String parameterType = fMethodParameterNode.getType();
		String webElementType = getWebElementValue(parameterType);

		refreshControls(webElementType);
	}

	private String getWebElementValue(String parameterType) {

		NodePropertyDefs.PropertyId propertyId = NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE;

		String webElementValue = fMethodParameterNode.getPropertyValue(propertyId);

		if (!NodePropertyDefs.isOneOfPossibleValues(webElementValue, propertyId, parameterType)) {
			webElementValue = null;
		}

		if (webElementValue == null) {
			webElementValue = NodePropertyDefs.getPropertyDefaultValue(propertyId, parameterType);
			fMethodParameterNode.setPropertyValue(fWebElementTypePropertyId, webElementValue);
		}

		return webElementValue;
	}

	private void refreshControls(String webElementType) {

		refreshWebElementTypeCombo(webElementType);
		refreshOptionalCheckBox(webElementType);
		refreshFindByTypeAndValue(webElementType);
		refreshAction(webElementType);
	}

	private void refreshWebElementTypeCombo(String value) {

		String parentValue = fMethodParameterNode.getType();

		String[] possibleValues = 
				NodePropertyDefs.getPossibleValues(
						fWebElementTypePropertyId, parentValue, fMethodParameterNode.isExpected());

		refreshCombo(fWebElementTypeCombo, possibleValues, parentValue, value);
	}

	private static void refreshCombo(Combo combo, String[] possibleValues, String parentValue, String value) {

		combo.setItems(possibleValues);

		boolean isOneOfPossibleValues = StringTabHelper.isOneOfValues(value, possibleValues);

		if (value != null && isOneOfPossibleValues) {
			combo.setText(value);
			return;
		}

		String firstValue = StringTabHelper.getFirstValue(possibleValues);
		if (firstValue != null) {
			combo.setText(firstValue);
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

		MethodParameterNode methodParameterNode = (MethodParameterNode)fMethodParameterNode;

		if (!methodParameterNode.isExpected()) {
			return false;
		}

		if (!NodePropertyDefs.isOptionalAvailable(webElementType)) {
			return false;
		}		

		return true;
	}

	private void refreshFindByTypeAndValue(String webElementType) {

		if (!isChildOfWebElementAvailable(webElementType)) {
			return;
		}

		refreshfFindElemTypeCombo(webElementType);
		refreshfFindByValueText();
	}

	private void refreshfFindElemTypeCombo(String webElementType) {
		String currentPropertyValue = 
				fMethodParameterNode.getPropertyValue(fFindByElemTypePropertyId);

		String[] possibleValues = NodePropertyDefs.getPossibleValues(fFindByElemTypePropertyId, webElementType);

		refreshCombo(fFindByElemTypeCombo, possibleValues, webElementType, currentPropertyValue);
	}

	private boolean isChildOfWebElementAvailable(String webElementType) {
		if (StringHelper.isNullOrEmpty(webElementType)) {
			return false;
		}
		if (!NodePropertyDefs.isFindByAvailable(webElementType)) {
			return false;
		}
		return true;
	}

	private void refreshfFindByValueText() {
		String value = fMethodParameterNode.getPropertyValue(fFindByElemValuePropertyId);

		if (value == null) {
			fFindByElemValueText.setText("");
		} else {
			fFindByElemValueText.setText(value);
		}
	}

	private void refreshAction(String webElementType) {

		if (!isChildOfWebElementAvailable(webElementType)) {
			return;
		}

		if (!NodePropertyDefs.isActionAvailable(webElementType)) {
			return;
		}		

		refreshActionCombo(webElementType);
	}

	private void refreshActionCombo(String webElementType) {

		String currentPropertyValue = 
				fMethodParameterNode.getPropertyValue(fActionPropertyId);

		String[] possibleValues = NodePropertyDefs.getPossibleValues(fActionPropertyId, webElementType);

		refreshCombo(fActionCombo, possibleValues, webElementType, currentPropertyValue);
	}	

	private class ElementTypeChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {

			String webElementType = fWebElementTypeCombo.getText();
			fAbstractParameterInterface.setProperty(fWebElementTypePropertyId, webElementType);

			refreshFindByTypeAndValue(webElementType);
			refreshAction(webElementType);
		}
	}

	private class FindByChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fAbstractParameterInterface.setProperty(fFindByElemTypePropertyId, fFindByElemTypeCombo.getText());
		}
	}

	private class FindByValueChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fAbstractParameterInterface.setProperty(fFindByElemValuePropertyId, fFindByElemValueText.getText());
		}
	}	

	private class ActionChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fAbstractParameterInterface.setProperty(fActionPropertyId, fActionCombo.getText());
		}
	}

	private class OptionalChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			String isOptionalStr = BooleanHelper.toString(fOptionalCheckbox.getSelection());
			fAbstractParameterInterface.setProperty(fOptionalPropertyId, isOptionalStr);
		}
	}	

}
