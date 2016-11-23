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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class WebParameterSection extends BasicSection {

	AbstractParameterInterface fAbstractParameterInterface;

	private FormObjectToolkit fFormObjectToolkit;
	private Combo fElementTypeCombo;
	private Combo fFindByCombo;

	private final NodePropertyDefs.PropertyId fParameterTypePropertyId = NodePropertyDefs.PropertyId.PROPERTY_PARAMETER_TYPE;
	private final NodePropertyDefs.PropertyId fFindByPropertyId = NodePropertyDefs.PropertyId.FIND_BY;


	public WebParameterSection(ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			AbstractParameterInterface abstractParameterInterface,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, StyleDistributor.getSectionStyle());

		fAbstractParameterInterface = abstractParameterInterface;

		fFormObjectToolkit = new FormObjectToolkit(getToolkit());

		setText("Web runner properties");
		Composite clientComposite = getClientComposite();

		Composite gridComposite1 = fFormObjectToolkit.createGridComposite(clientComposite, 2);
		fFormObjectToolkit.paintBorders(gridComposite1);
		createElementTypeCombo(gridComposite1);
	}

	private void createElementTypeCombo(Composite gridComposite) {
		fFormObjectToolkit.createLabel(gridComposite, "Element type");
		fElementTypeCombo = fFormObjectToolkit.createGridCombo(gridComposite, new ElementTypeChangedAdapter());
		initializeComboByProperty(fElementTypeCombo, fParameterTypePropertyId);

		fFormObjectToolkit.createLabel(gridComposite, "Find by");
		fFindByCombo = fFormObjectToolkit.createGridCombo(gridComposite, new FindByChangedAdapter());
		initializeComboByProperty(fFindByCombo, fFindByPropertyId);
	}

	public static void initializeComboByProperty(Combo combo, NodePropertyDefs.PropertyId propertyId) {
		combo.setItems(NodePropertyDefs.getPropertyPossibleValues(propertyId));

		String defaultValue = NodePropertyDefs.getPropertyDefaultValue(propertyId);
		if (defaultValue != null) {
			combo.setText(defaultValue);
		}
	}	

	public void refresh() {
		AbstractParameterNode abstractParameterNode = fAbstractParameterInterface.getTarget();

		refreshComboByProperty(fParameterTypePropertyId, fElementTypeCombo, abstractParameterNode);
		refreshComboByProperty(fFindByPropertyId, fFindByCombo, abstractParameterNode);
	}

	private void refreshComboByProperty(
			NodePropertyDefs.PropertyId propertyId, Combo combo, AbstractParameterNode abstractParameterNode) {

		String value = abstractParameterNode.getPropertyValue(propertyId);
		if (value != null) {
			combo.setText(value);
		}		
	}	

	private class ElementTypeChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fAbstractParameterInterface.setProperty(fParameterTypePropertyId, fElementTypeCombo.getText());
		}
	}

	private class FindByChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fAbstractParameterInterface.setProperty(fFindByPropertyId, fFindByCombo.getText());
		}
	}	

}
