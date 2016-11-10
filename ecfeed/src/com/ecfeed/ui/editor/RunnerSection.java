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

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodInterface;

public class RunnerSection extends BasicSection  {

	MethodInterface fMethodInterface;
	private FormObjectToolkit fFormObjectToolkit;
	private Combo fRunnersCombo;
	private final NodePropertyDefs.PropertyId fRunnerPropertyId = NodePropertyDefs.PropertyId.METHOD_RUNNER;

	public RunnerSection(ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			MethodInterface methodIf,
			IFileInfoProvider fileInfoProvider) {
		super(sectionContext, updateContext, fileInfoProvider, StyleDistributor.getSectionStyle());

		fMethodInterface = methodIf;
		fFormObjectToolkit = new FormObjectToolkit(getToolkit());

		setText("Runner");

		Composite clientComposite = getClientComposite();

		Composite gridComposite = fFormObjectToolkit.createGridComposite(clientComposite, 2);
		fFormObjectToolkit.paintBorders(gridComposite);

		fFormObjectToolkit.createLabel(gridComposite, "Runner");
		fRunnersCombo = fFormObjectToolkit.createGridCombo(gridComposite, new RunnerChangedAdapter());

		fRunnersCombo.setItems(NodePropertyDefs.getPropertyPossibleValues(fRunnerPropertyId)); 
		fRunnersCombo.setText(NodePropertyDefs.getPropertyDefaultValue(fRunnerPropertyId));		
	}

	public void refresh() {
		MethodNode methodNode = fMethodInterface.getTarget();
		fRunnersCombo.setText(methodNode.getPropertyValue(fRunnerPropertyId));
	}

	private class RunnerChangedAdapter extends AbstractSelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fMethodInterface.setProperty(fRunnerPropertyId, fRunnersCombo.getText());
		}
	}

}
