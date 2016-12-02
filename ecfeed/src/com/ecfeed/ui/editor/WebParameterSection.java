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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Text; TODO
import org.eclipse.swt.widgets.Label;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.NodePropertyDefs;
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

	private Combo fWebElementTypeCombo = null;

	private Label fFindByLabel = null;
	private Combo fFindByElemTypeCombo = null;

	//	private Text fFindByElemValueText = null;
	//	private Combo fActionCombo = null;

	private final NodePropertyDefs.PropertyId fWebElementTypePropertyId = NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE;

	private final NodePropertyDefs.PropertyId fFindByElemTypePropertyId = NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT;
	//	private final NodePropertyDefs.PropertyId fFindByElemValuePropertyId = NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_VALUE_OF_ELEMENT;
	//	private final NodePropertyDefs.PropertyId fActionPropertyId = NodePropertyDefs.PropertyId.PROPERTY_ACTION;

	public WebParameterSection(ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			AbstractParameterInterface abstractParameterInterface,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, StyleDistributor.getSectionStyle());

		fAbstractParameterInterface = abstractParameterInterface;

		fFormObjectToolkit = new FormObjectToolkit(getToolkit());

		setText("Web runner properties");
		fClientComposite = getClientComposite();

		fGridComposite = fFormObjectToolkit.createGridComposite(fClientComposite, 2);
		fFormObjectToolkit.paintBorders(fGridComposite);
		createControls(fGridComposite);
	}

	private void createControls(Composite gridComposite) {

		fFormObjectToolkit.createLabel(fGridComposite, "Element type");
		fWebElementTypeCombo = fFormObjectToolkit.createReadOnlyGridCombo(fGridComposite, new ElementTypeChangedAdapter());

		//		createFindByLabelAndCombo();
		//		fFindByLabel.dispose();
		//		fFindByElemTypeCombo.dispose();

		//		fFormObjectToolkit.createLabel(gridComposite, "Find element by ");
		//		fFindByElemTypeCombo = fFormObjectToolkit.createReadOnlyGridCombo(gridComposite, new FindByChangedAdapter());
		//		initializeComboByProperty(fFindByElemTypeCombo, fFindByElemTypePropertyId);

		//		fFormObjectToolkit.createLabel(gridComposite, "Using ");
		//		fFindByElemValueText = fFormObjectToolkit.createGridText(gridComposite, new FindByValueChangedAdapter());

		//		fFormObjectToolkit.createLabel(gridComposite, "Action ");
		//		fActionCombo = fFormObjectToolkit.createReadOnlyGridCombo(gridComposite, new ActionChangedAdapter());
		//		initializeComboByProperty(fActionCombo, fActionPropertyId);
	}

	//	public static void initializeComboByProperty(Combo combo, NodePropertyDefs.PropertyId propertyId) {
	//		combo.setItems(NodePropertyDefs.getPossibleValues(propertyId));
	//
	//		String defaultValue = NodePropertyDefs.getPropertyDefaultValue(propertyId);
	//		if (defaultValue != null) {
	//			combo.setText(defaultValue);
	//		}
	//	}	

	public void refresh() {
		fAbstractParameterNode = fAbstractParameterInterface.getTarget();
		refreshWebElementType();

		//		refreshComboByProperty(fWebElementTypePropertyId, fWebElementTypeCombo, abstractParameterNode);
		//		refreshComboByProperty(fFindByElemTypePropertyId, fFindByElemTypeCombo, abstractParameterNode);
		//		refreshTextByProperty(fFindByElemValuePropertyId, fFindByElemValueText, abstractParameterNode);
		//		refreshComboByProperty(fActionPropertyId, fActionCombo, abstractParameterNode);
	}

	private void refreshWebElementType() {
		String parameterType = fAbstractParameterNode.getType();
		String webElementType = getWebElementValue(parameterType);
		refreshWebElementTypeCombo(webElementType);
		refreshFindBy(webElementType);
	}

	private String getWebElementValue(String parameterType) {
		NodePropertyDefs.PropertyId propertyId = NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE;

		String webElementValue = fAbstractParameterNode.getPropertyValue(propertyId);
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

	private void refreshFindBy(String webElementType) {

		disposeFindByElemControls();

		if (StringHelper.isNullOrEmpty(webElementType)) {
			return;
		}
		if (!NodePropertyDefs.isFindByAvailable(webElementType)) {
			return;
		}

		createFindByElemControls();
		refreshfFindElemTypeCombo(webElementType);

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

	//	private void refreshComboByProperty(
	//			NodePropertyDefs.PropertyId propertyId, Combo combo, AbstractParameterNode abstractParameterNode) {
	//
	//		if (combo == null) {
	//			return;
	//		}
	//
	//		String value = abstractParameterNode.getPropertyValue(propertyId);
	//		if (value != null) {
	//			combo.setText(value);
	//			return;
	//		}
	//		combo.setText(NodePropertyDefs.getEmptyElement());
	//	}	

	//	private void refreshTextByProperty(
	//			NodePropertyDefs.PropertyId propertyId, Text text, AbstractParameterNode abstractParameterNode) {
	//
	//		if (text == null) {
	//			return;
	//		}
	//
	//		String value = abstractParameterNode.getPropertyValue(propertyId);
	//		if (value != null) {
	//			text.setText(value);
	//			return;
	//		}		
	//		text.setText(NodePropertyDefs.getEmptyElement());
	//	}	

	private class ElementTypeChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			String webElementType = fWebElementTypeCombo.getText();
			fAbstractParameterInterface.setProperty(fWebElementTypePropertyId, webElementType);
			refreshFindBy(webElementType);
		}
	}

	private class FindByChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fAbstractParameterInterface.setProperty(fFindByElemTypePropertyId, fFindByElemTypeCombo.getText());
		}
	}

	//	private class FindByValueChangedAdapter extends AbstractSelectionAdapter {
	//		@Override
	//		public void widgetSelected(SelectionEvent e) {
	//			fAbstractParameterInterface.setProperty(fFindByElemValuePropertyId, fFindByElemValueText.getText());
	//		}
	//	}	

	//	private class ActionChangedAdapter extends AbstractSelectionAdapter {
	//		@Override
	//		public void widgetSelected(SelectionEvent e) {
	//			fAbstractParameterInterface.setProperty(fActionPropertyId, fActionCombo.getText());
	//		}
	//	}	
}
