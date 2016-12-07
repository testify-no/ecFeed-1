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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class WebParameterSection extends BasicSection {

	AbstractParameterInterface fAbstractParameterInterface;
	AbstractParameterNode fAbstractParameterNode;

	private FormObjectToolkit fFormObjectToolkit;
	private Composite fClientComposite;
	private Composite fGridComposite;

	private Combo fWebElementTypeCombo;
	private Button fOptionalCheckbox;

	private Label fFindBySpacer;
	private Label fFindByLabel;
	private Combo fFindByElemTypeCombo;

	private Label fFindByElemValueLabel;
	private Text fFindByElemValueText;

	private Label fActionLabel = null;
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

		fGridComposite = fFormObjectToolkit.createGridComposite(fClientComposite, 5);
		createControls(fGridComposite);
		fFormObjectToolkit.paintBorders(fGridComposite);
	}

	private void createControls(Composite gridComposite) {
		fAbstractParameterNode = fAbstractParameterInterface.getTarget();

		fFormObjectToolkit.createLabel(fGridComposite, "Element type");
		fWebElementTypeCombo = fFormObjectToolkit.createReadOnlyGridCombo(fGridComposite, new ElementTypeChangedAdapter());

		fFormObjectToolkit.createEmptyLabel(fGridComposite);
		fFormObjectToolkit.createEmptyLabel(fGridComposite);

		fOptionalCheckbox = fFormObjectToolkit.createGridCheckBox(fGridComposite, "Optional", new OptionalChangedAdapter() );
		fOptionalCheckbox.setVisible(false);
	}

	public void refresh() {
		fAbstractParameterNode = fAbstractParameterInterface.getTarget();
		refreshWebElementTypeWithChildren();
	}

	private void refreshWebElementTypeWithChildren() {

		String parameterType = fAbstractParameterNode.getType();
		String webElementType = getWebElementValue(parameterType);

		refreshWebElementTypeCombo(webElementType);
		refreshOptionalCheckBox(webElementType);
		refreshFindByTypeAndValue(webElementType);
		refreshAction(webElementType);
	}

	private String getWebElementValue(String parameterType) {

		NodePropertyDefs.PropertyId propertyId = NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE;

		String webElementValue = fAbstractParameterNode.getPropertyValue(propertyId);

		if (!NodePropertyDefs.isOneOfPossibleValues(webElementValue, propertyId, parameterType)) {
			webElementValue = null;
		}

		if (webElementValue == null) {
			webElementValue = NodePropertyDefs.getPropertyDefaultValue(propertyId, parameterType);
			fAbstractParameterNode.setPropertyValue(fWebElementTypePropertyId, webElementValue);
		}

		return webElementValue;
	}

	private void refreshWebElementTypeCombo(String webElementValue) {
		refreshCombo(fWebElementTypeCombo, NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE, 
				fAbstractParameterNode.getType(), webElementValue);
	}

	private void refreshOptionalCheckBox(String webElementType) {

		if (!(fAbstractParameterNode instanceof MethodParameterNode)) {
			fOptionalCheckbox.setVisible(false);
			return;
		}

		MethodParameterNode methodParameterNode = (MethodParameterNode)fAbstractParameterNode;

		if (!methodParameterNode.isExpected()) {
			fOptionalCheckbox.setVisible(false);
			return;
		}

		String currentPropertyValue = 
				fAbstractParameterNode.getPropertyValue(fOptionalPropertyId);

		boolean isChecked = BooleanHelper.parseBoolean(currentPropertyValue);

		fOptionalCheckbox.setSelection(isChecked);
		fOptionalCheckbox.setVisible(true);
	}

	private void refreshFindByTypeAndValue(String webElementType) {

		disposeFindByElemControls();
		disposeFindBySpacer();
		disposeFindByValueControls();

		if (!isChildOfWebElementAvailable(webElementType)) {
			return;
		}

		refreshFindByType(webElementType);
		fFindBySpacer = fFormObjectToolkit.createEmptyLabel(fGridComposite);
		refreshFindByValue(webElementType);

		fGridComposite.pack();
		fGridComposite.layout(true);		
	}

	private void disposeFindByElemControls() {
		if (fFindByLabel != null) {
			fFindByLabel.dispose();
		}
		if (fFindByElemTypeCombo != null) {
			fFindByElemTypeCombo.dispose();
		}
	}

	private void disposeFindByValueControls() {

		if (fFindByElemValueLabel != null) {
			fFindByElemValueLabel.dispose();
		}
		if (fFindByElemValueText != null) {
			fFindByElemValueText.dispose();
		}
	}

	private void disposeFindBySpacer() {

		if (fFindBySpacer != null) {
			fFindBySpacer.dispose();
		}
	}	


	private void refreshFindByType(String webElementType) {

		createFindByElemControls();
		refreshfFindElemTypeCombo(webElementType);
	}

	private void createFindByElemControls() {
		fFindByLabel = fFormObjectToolkit.createLabel(fGridComposite, "Find element by ");
		fFindByElemTypeCombo = fFormObjectToolkit.createReadOnlyGridCombo(fGridComposite, new FindByChangedAdapter());
	}

	private void refreshfFindElemTypeCombo(String webElementType) {
		String currentPropertyValue = 
				fAbstractParameterNode.getPropertyValue(fFindByElemTypePropertyId);

		refreshCombo(fFindByElemTypeCombo, fFindByElemTypePropertyId, 
				webElementType, currentPropertyValue);
	}

	private static void refreshCombo(Combo combo, NodePropertyDefs.PropertyId propertyId, String parentValue, String value) {

		combo.setItems(NodePropertyDefs.getPossibleValues(propertyId, parentValue));

		if (value != null && NodePropertyDefs.isOneOfPossibleValues(value, propertyId, parentValue)) {
			combo.setText(value);
			return;
		}

		String defaultValue = NodePropertyDefs.getPropertyDefaultValue(propertyId, parentValue);
		if (defaultValue != null) {
			combo.setText(defaultValue);
		}		
	}

	private void refreshFindByValue(String webElementType) {

		createFindByValueControls();
		refreshfFindByValueText();
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

	private void createFindByValueControls() {
		fFindByElemValueLabel = fFormObjectToolkit.createLabel(fGridComposite, "Using ");
		fFindByElemValueText = fFormObjectToolkit.createGridText(fGridComposite, new FindByValueChangedAdapter());
	}

	private void refreshfFindByValueText() {
		String value = fAbstractParameterNode.getPropertyValue(fFindByElemValuePropertyId);

		if (value == null) {
			fFindByElemValueText.setText("");
		} else {
			fFindByElemValueText.setText(value);
		}
	}

	private void refreshAction(String webElementType) {

		disposeActionControls();

		if (!isChildOfWebElementAvailable(webElementType)) {
			return;
		}

		if (!NodePropertyDefs.isActionAvailable(webElementType)) {
			return;
		}		

		createActionControls();
		refreshActionCombo(webElementType);

		fGridComposite.pack();
		fGridComposite.layout(true);		
	}

	private void disposeActionControls() {

		if (fActionLabel != null) {
			fActionLabel.dispose();
		}
		if (fActionLabel != null) {
			fActionCombo.dispose();
		}
	}

	private void createActionControls() {
		fActionLabel = fFormObjectToolkit.createLabel(fGridComposite, "Action ");
		fActionCombo = fFormObjectToolkit.createReadOnlyGridCombo(fGridComposite, new ActionChangedAdapter());
	}	

	private void refreshActionCombo(String webElementType) {
		String currentPropertyValue = 
				fAbstractParameterNode.getPropertyValue(fActionPropertyId);

		refreshCombo(fActionCombo, fActionPropertyId, 
				webElementType, currentPropertyValue);
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
