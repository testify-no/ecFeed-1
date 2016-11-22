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

import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;

public class ParameterWebSection extends BasicSection  {

	private FormObjectToolkit fFormObjectToolkit;
	private Combo fElementTypeCombo;

	private final NodePropertyDefs.PropertyId fParmeterTypePropertyId = NodePropertyDefs.PropertyId.PARAMETER_TYPE;

	public ParameterWebSection(ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, StyleDistributor.getSectionStyle());

		fFormObjectToolkit = new FormObjectToolkit(getToolkit());

		setText("Web driver settings");
		Composite clientComposite = getClientComposite();

		Composite gridComposite1 = fFormObjectToolkit.createGridComposite(clientComposite, 2);
		fFormObjectToolkit.paintBorders(gridComposite1);
		createElementTypeCombo(gridComposite1);
	}

	private void createElementTypeCombo(Composite gridComposite) {
		fFormObjectToolkit.createLabel(gridComposite, "Element type");
		fElementTypeCombo = fFormObjectToolkit.createGridCombo(gridComposite, new ElementTypeChangedAdapter());
		fElementTypeCombo.setItems(NodePropertyDefs.getPropertyPossibleValues(fParmeterTypePropertyId)); 
		fElementTypeCombo.setText(NodePropertyDefs.getPropertyDefaultValue(fParmeterTypePropertyId));
	}

	private class ElementTypeChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
		}
	}

}
